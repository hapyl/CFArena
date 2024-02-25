package me.hapyl.fight.game.experience;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.reward.*;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Experience extends DependencyInjector<Main> {

    private final TreeMap<Long, ExperienceLevel> experienceLevelMap;

    private final Color GRADIENT_COLOR_1 = new Color(253, 29, 29);
    private final Color GRADIENT_COLOR_2 = new Color(252, 210, 69);
    private final String LEVEL_UP_GRADIENT = new Gradient("LEVEL UP!")
            .makeBold()
            .rgb(
                    GRADIENT_COLOR_1,
                    GRADIENT_COLOR_2,
                    Interpolators.LINEAR
            );

    private final int progressBarCount = 40;

    public Experience(Main main) {
        super(main);

        experienceLevelMap = Maps.newTreeMap();
        setupMap();
        setupRewards();
    }

    /**
     * Returns total experience required to reach lvl, or {@link Long#MAX_VALUE} is level is maxed.
     *
     * @param lvl - level to get exp required for.
     * @return exp required reaching level.
     */
    public long getExpRequired(long lvl) {
        final ExperienceLevel level = experienceLevelMap.get(lvl);

        if (level != null) {
            return level.getExpRequired();
        }

        return Long.MAX_VALUE; // max value so we can accumulate exp further
    }

    /**
     * Returns true if player experience is equals or greater than the next level requirement.
     *
     * @param player - Player.
     * @return true if a player can level up; false otherwise.
     */
    public boolean canLevelUp(Player player) {
        final ExperienceEntry exp = getDatabaseEntry(player);
        final long nextLevel = exp.get(ExperienceEntry.Type.LEVEL) + 1;
        final long totalExp = exp.get(ExperienceEntry.Type.EXP);

        return totalExp >= getExpRequired(nextLevel);
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

        // Give all previous rewards
        for (long lvl = playerLvl; lvl > 0; lvl--) {
            final List<Reward> rewards = getRewards(lvl);
            if (rewards == null) {
                continue;
            }

            for (Reward reward : rewards) {
                // Don't give one time rewards, such as coins etc

                // FIXME (hapyl): 029, Sep 29:
                //  This could really use reward entry and storing claimed reward,
                //  but then all rewards must be named or ID's somehow.
                //  And what about daily or repeatable rewards?
                if (reward instanceof OneTimeReward) {
                    continue;
                }

                reward.grant(player);
            }
        }

        // Fix achievement
        Achievements.LEVEL_TIERED.setProgress(player, (int) playerLvl);
    }

    @Nonnull
    public ExperienceColor getExperienceColor(long level) {
        return ExperienceColor.getByLevel(level);
    }

    @Nonnull
    public String getExpPrefix(long level) {
        return ChatColor.DARK_GRAY + "[" + getExperienceColor(level).getColor() + level + ChatColor.DARK_GRAY + "]";
    }

    /**
     * Performs a player level up.
     *
     * @param player - Player.
     * @param force  - Whenever to force player level up, even if player does not meet the requirements.
     * @return true if player was leveled up; false otherwise.
     */
    public boolean levelUp(Player player, boolean force) {
        if (!canLevelUp(player) && !force) {
            return false;
        }

        final ExperienceEntry entry = getDatabaseEntry(player);
        final long currentLevel = entry.get(ExperienceEntry.Type.LEVEL);
        final long toLevel = getLevelEnoughExp(entry.get(ExperienceEntry.Type.EXP));

        if (currentLevel >= toLevel) {
            return false;
        }

        entry.set(ExperienceEntry.Type.LEVEL, toLevel);

        // Grant rewards
        for (long level = currentLevel + 1; level <= toLevel; level++) {
            final List<Reward> rewards = getRewards(level);

            if (rewards != null) {
                for (Reward reward : rewards) {
                    reward.grant(player);
                }
            }
        }

        // Fix rewards
        fixRewards(player);

        // Display reward message and sound
        // Don't display if leveling up to level 1
        if (toLevel <= 1) {
            return false;
        }

        displayRewardMessage(player, toLevel);
        return true;
    }

    public long getLevelEnoughExp(long exp) {
        final TreeMap<Long, ExperienceLevel> treeMap = new TreeMap<>(experienceLevelMap);

        for (Long level : treeMap.descendingKeySet()) {
            final ExperienceLevel experienceLevel = treeMap.get(level);

            if (exp >= experienceLevel.getExpRequired()) {
                return experienceLevel.getLevel();
            }
        }

        return 1;
    }

    public void displayRewardMessage(Player player, long level) {
        Chat.sendMessage(player, "");

        Chat.sendCenterMessage(player, LEVEL_UP_GRADIENT);
        Chat.sendCenterMessage(player, "&eYou are now level %s!", level);

        final List<Reward> rewards = getRewards(level);

        Chat.sendMessage(player, "");
    }

    public long getMaxLevel() {
        return ExperienceEntry.Type.LEVEL.getMaxValue();
    }

    @Nullable
    public List<Reward> getRewards(long level) {
        if (level < 1 || level > getMaxLevel()) {
            return null;
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
        return Numbers.clamp((float) (getExpScaled(player)) / (float) (getExpScaledNext(player)), 0.0f, 1.0f);
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
        return Manager.current().getOrCreateProfile(player).getDatabase().experienceEntry;
    }

    public long getLevel(Player player) {
        return getDatabaseEntry(player).get(ExperienceEntry.Type.LEVEL);
    }

    @Nullable
    public ExperienceLevel getPlayerLevel(Player player) {
        return getLevel(getLevel(player));
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
        final int bars = (int) (progress * progressBarCount);
        final int empty = progressBarCount - bars;

        final String bar = "&a" + Strings.repeat("|", bars);
        final String emptyBar = "&c" + Strings.repeat("|", empty);

        return bar + emptyBar;
    }

    /**
     * Returns a feed of 5 closest levels.
     * The array <b>always</b> contains 5 levels.
     * <p>
     * Array structure:
     * <pre>
     *     [n-1, n, n+1, n+2, n+3]
     * </pre>
     *
     * @param currentLevel - Current level.
     * @return an arrow with 5 closest {@link ExperienceLevel} to the currentLevel.
     */
    @Nonnull
    public ExperienceLevel[] getLevelFeed(long currentLevel) {
        final ExperienceLevel[] levels = new ExperienceLevel[5];
        final long feedEnd = Numbers.clamp(currentLevel + 3, 5, getMaxLevel());

        for (int i = 0; i < 5; i++) {
            final long level = Math.max(feedEnd - i, getMinLevel());
            levels[4 - i] = getLevel(level);
        }

        return levels;
    }

    public long getMinLevel() {
        return 1;
    }

    @Nonnull
    protected Map<Long, ExperienceLevel> getExperienceLevelMap() {
        return experienceLevelMap;
    }

    private void setupMap() {
        long currentExp = 0;

        for (long level = 1; level <= getMaxLevel(); level++) {
            if (level > 1) {
                currentExp = (long) (currentExp * 1.2 + 100);
                if (currentExp % 10 < 5) {
                    currentExp += 5 - currentExp % 10;
                }
                else {
                    currentExp += 10 - currentExp % 10;
                }
            }

            final boolean isPrestige = level % 5 == 0;
            final ExperienceColor color = getExperienceColor(level);

            final ExperienceLevel experienceLevel = new ExperienceLevel(level, currentExp);

            if (isPrestige) {
                experienceLevel.addReward(new DisplayReward("Prestige Color") {
                    @Nonnull
                    @Override
                    public RewardDisplay getDisplay(@Nonnull Player player) {
                        return RewardDisplay.of(color + " &7prestige color");
                    }
                });
            }

            experienceLevelMap.put(level, experienceLevel);
        }
    }

    private void setupRewards() {
        // Coins rewards
        experienceLevelMap.forEach((lvl, level) -> {
            final CurrencyReward reward = new CurrencyReward();

            reward.with(CurrencyType.COINS, 1000 * lvl);

            if (level.isPrestige()) {
                reward.with(CurrencyType.RUBY, 1);
            }

            level.addReward(reward);
        });

        // Hero unlocks
        for (Heroes value : Heroes.values()) {
            final long minLevel = value.getHero().getMinimumLevel();
            if (minLevel > 0) {
                final ExperienceLevel level = experienceLevelMap.get(minLevel);
                if (level != null) {
                    level.addReward(new HeroUnlockReward(value));
                }
            }
        }

        // Manual rewards
        // Keep manual rewards last for consistency
        setReward(1, Reward.cosmetics(Cosmetics.PEACE));
        setReward(2, Reward.cosmetics(Cosmetics.EMERALD_EXPLOSION));
    }

    private void setReward(int level, Reward reward) {
        final ExperienceLevel exp = experienceLevelMap.get((long) level);
        if (exp != null) {
            exp.addReward(reward);
        }
    }

    private void updateProgressBar(Player player) {
        final float progress = getProgress(player);

        player.setLevel((int) getLevel(player));
        player.setExp(progress);
    }
}
