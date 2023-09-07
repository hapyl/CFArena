package me.hapyl.fight.game.experience;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.reward.HeroUnlockReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Experience extends DependencyInjector<Main> {

    // TODO (hapyl): 005, Sep 5: Add level colors like hypixel?
    public final byte MAX_LEVEL = 50;

    private final Map<Long, ExperienceLevel> experienceLevelMap;
    private final Color GRADIENT_COLOR_1 = new Color(253, 29, 29);
    private final Color GRADIENT_COLOR_2 = new Color(252, 210, 69);
    private final String LEVEL_UP_GRADIENT = new Gradient("LEVEL UP!")
            .makeBold()
            .rgb(
                    GRADIENT_COLOR_1,
                    GRADIENT_COLOR_2,
                    Interpolators.LINEAR
            );

    /**
     * Instantiate Experience manager.
     */
    public Experience(Main main) {
        super(main);
        this.experienceLevelMap = Maps.newLinkedHashMap();
        this.setupMap();
        this.setupRewards();
    }

    /**
     * Returns total experience required to reach lvl, or {@link Long#MAX_VALUE} is level is maxed.
     *
     * @param lvl - level to get exp required for.
     * @return exp required reaching level.
     */
    public long getExpRequired(long lvl) {
        if (experienceLevelMap.containsKey(lvl)) {
            return experienceLevelMap.get(lvl).getExpRequired();
        }
        return Long.MAX_VALUE; // max value so we can accumulate exp further
    }

    public boolean canLevelUp(Player player) {
        final ExperienceEntry exp = getDatabaseEntry(player);
        final long lvl = exp.get(ExperienceEntry.Type.LEVEL) + 1;
        final long totalExp = exp.get(ExperienceEntry.Type.EXP);

        return totalExp >= getExpRequired(lvl);
    }

    /**
     * This iterating through all rewards and grants
     * or revoking them depending on player level.
     * <p>
     * Needed in case of new reward to grant, or admin
     * manipulations.
     */
    public void fixRewards(Player player) {
        final ExperienceEntry entry = getDatabaseEntry(player);
        final long playerLvl = entry.get(ExperienceEntry.Type.LEVEL);

        experienceLevelMap.values().forEach(exp -> {
            for (Reward reward : exp.getRewards()) {
                reward.revokeReward(player);

                if (playerLvl >= exp.getLevel()) {
                    reward.grantReward(player);
                }
            }
        });
    }

    public boolean levelUp(Player player, boolean force) {
        if (!canLevelUp(player) && !force) {
            return false;
        }

        final ExperienceEntry exp = getDatabaseEntry(player);

        final long currentLevel = exp.get(ExperienceEntry.Type.LEVEL);
        final long nextLevel = Math.min(currentLevel + 1, MAX_LEVEL);

        if (nextLevel <= 1) {
            // don't level to lvl 1
            return false;
        }

        exp.set(ExperienceEntry.Type.LEVEL, nextLevel);
        fixRewards(player);

        displayRewardMessage(player, nextLevel);
        return true;
    }

    public void displayRewardMessage(Player player, long level) {
        Chat.sendMessage(player, "");

        Chat.sendCenterMessage(player, LEVEL_UP_GRADIENT);
        Chat.sendCenterMessage(player, "&eYou are now level %s!", level);

        final List<Reward> rewards = getRewards(level);

        Chat.sendMessage(player, "");
    }

    public List<Reward> getRewards(long level) {
        if (level < 1 || level > MAX_LEVEL) {
            return Lists.newArrayList();
        }
        return experienceLevelMap.get(level).getRewards();
    }

    /**
     * Updates player's experience data UI.
     */
    public void triggerUpdate(Player player) {
        updateProgressBar(player);
    }

    /**
     * Returns the progress of the player to the next level.
     *
     * @param player - player to get progress for.
     * @return progress from 0 to 1.
     */
    public float getProgress(Player player) {
        return (float) (getExpScaled(player)) / (float) (getExpScaledNext(player));
    }

    /**
     * Returns the level of the player, scaled to the current level.
     *
     * @param player - player to get level for.
     * @return level of the player.
     */
    public long getExpScaled(Player player) {
        final ExperienceEntry experience = getDatabaseEntry(player);
        final long playerExp = experience.get(ExperienceEntry.Type.EXP);
        final long previousLvlExp = getExpRequired(getLevel(player));

        return playerExp - previousLvlExp;
    }

    /**
     * Returns the level of the player, scaled to the next level.
     *
     * @param player - player to get level for.
     * @return level of the player.
     */
    public long getExpScaledNext(Player player) {
        final long previousLvlExp = getExpRequired(getLevel(player));
        final long nextLvlExp = getExpRequired(getLevel(player) + 1);

        return nextLvlExp - previousLvlExp;
    }

    public ExperienceEntry getDatabaseEntry(Player player) {
        return Manager.current().getOrCreateProfile(player).getDatabase().getExperienceEntry();
    }

    public Map<Long, ExperienceLevel> getLevels() {
        return experienceLevelMap;
    }

    public long getLevel(Player player) {
        return getDatabaseEntry(player).get(ExperienceEntry.Type.LEVEL);
    }

    @Nullable
    public ExperienceLevel getLevel(long index) {
        return experienceLevelMap.get(index);
    }

    public long getExp(Player player) {
        return getDatabaseEntry(player).get(ExperienceEntry.Type.EXP);
    }

    public String getProgressBar(Player player) {
        final float progress = getProgress(player);
        final int bars = (int) (progress * 20);
        final int empty = 20 - bars;

        final String bar = "&a" + Strings.repeat("|", bars);
        final String emptyBar = "&c" + Strings.repeat("|", empty);

        return bar + emptyBar;
    }

    private void setupMap() {
        long currentExp = 0;
        for (long i = 1; i <= MAX_LEVEL; i++) {
            if (i > 1) {
                currentExp = (long) (currentExp * 1.2 + 100);
                if (currentExp % 10 < 5) {
                    currentExp += 5 - currentExp % 10;
                }
                else {
                    currentExp += 10 - currentExp % 10;
                }
            }

            final long points = (i > 1 && i % 5 == 0) ? 4 : 2;

            experienceLevelMap.put(i, new ExperienceLevel(i, currentExp));
        }
    }

    private void setupRewards() {
        final IntInt i = new IntInt(1);
        for (ExperienceLevel value : experienceLevelMap.values()) {
        }

        setReward(7, Reward.create("Random Test Reward"));

        for (Heroes value : Heroes.values()) {
            final long minLevel = value.getHero().getMinimumLevel();
            if (minLevel > 0) {
                final ExperienceLevel level = experienceLevelMap.get(minLevel);
                if (level != null) {
                    level.addReward(new HeroUnlockReward(value));
                }
            }
        }
    }

    private void setReward(int level, Reward reward) {
        final ExperienceLevel exp = experienceLevelMap.get((long) level);
        if (exp != null) {
            exp.addReward(reward);
        }
    }

    private void updateProgressBar(Player player) {
        final float progress = Numbers.clamp(getProgress(player), 0.0f, 1.0f);

        player.setLevel((int) getLevel(player));
        player.setExp(progress);
    }
}
