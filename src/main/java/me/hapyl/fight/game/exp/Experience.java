package me.hapyl.fight.game.exp;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.database.entry.ExperienceEntry;
import me.hapyl.fight.game.reward.ExpLevelUpReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class Experience {

    private final long MAX_LEVEL = 20;

    private final Map<Long, ExperienceLevel> experienceLevelMap;

    public Experience() {
        this.experienceLevelMap = Maps.newHashMap();
        this.setupMap();
        this.setupRewards();
    }

    private void setupMap() {
        long currentExp = 100;
        for (long i = 1; i <= MAX_LEVEL; i++) {
            if (i > 1) {
                currentExp += currentExp * 1.2;
                if (currentExp % 10 < 5) {
                    currentExp += 5 - currentExp % 10;
                }
                else {
                    currentExp += 10 - currentExp % 10;
                }
            }

            // This stores amount of
            final long points = (i > 1 && i % 5 == 0) ? 4 : 2;

            experienceLevelMap.put(i, new ExperienceLevel(i, currentExp).addReward(new ExpLevelUpReward(points)));
        }
    }

    /**
     * Manual process :|
     */
    private void setupRewards() {

    }

    public long getExpRequired(long lvl) {
        if (experienceLevelMap.containsKey(lvl)) {
            return experienceLevelMap.get(lvl).getExpRequired();
        }
        return Long.MAX_VALUE; // max value so we can accumulate exp further
    }

    public boolean canLevelUp(Player player) {
        final ExperienceEntry exp = getDatabaseEntry(player);
        final long lvl = exp.get(ExperienceEntry.Type.LEVEL);
        final long totalExp = exp.get(ExperienceEntry.Type.EXP);

        return totalExp >= getExpRequired(lvl);
    }

    /**
     * This iterates through all rewards and grants
     * or revokes them depending on players level.
     * <p>
     * Needed in case of new reward to grant, or admin
     * manipulations.
     */
    public void fixRewards(Player player) {

    }

    public boolean levelUp(Player player) {
        if (!canLevelUp(player)) {
            return false;
        }

        final ExperienceEntry exp = getDatabaseEntry(player);

        final long currentLevel = exp.get(ExperienceEntry.Type.LEVEL);
        final long nextLevel = Math.min(currentLevel + 1, MAX_LEVEL);

        exp.set(ExperienceEntry.Type.LEVEL, nextLevel);
        fixRewards(player);

        displayRewardMessage(player, nextLevel);
        return true;
    }

    public void displayRewardChatMessage(Player player, long level) {

    }

    private final String LEVEL_UP_GRADIENT = new Gradient("LEVEL UP!").makeBold()
            .rgb(
                    new Color(255, 5, 105),
                    new Color(10, 255, 239),
                    Interpolators.LINEAR
            );

    public void displayRewardMessage(Player player, long level) {
        Chat.sendMessage(player, "");

        Chat.sendMessage(player, LEVEL_UP_GRADIENT);

        Chat.sendMessage(player, "");
    }

    public List<Reward> getRewards(long level) {
        return experienceLevelMap.get(level).getRewards();
    }

    public void updateProgressBar(Player player) {
        final ExperienceEntry exp = getDatabaseEntry(player);
        final long playerLvl = exp.get(ExperienceEntry.Type.LEVEL);
        final long nextLvl = playerLvl + 1;
        final long totalExpNeededScaled = getExpRequired(nextLvl) - getExpRequired(playerLvl);
        final long currentExpScaled = exp.get(ExperienceEntry.Type.EXP) - getExpRequired(playerLvl);

        final float percent = Numbers.clamp((float) currentExpScaled / totalExpNeededScaled, 0.0f, 1.0f);

        player.setLevel((int) playerLvl);
        player.setExp(percent);
    }

    private ExperienceEntry getDatabaseEntry(Player player) {
        return Manager.current().getProfile(player).getDatabase().getExperienceEntry();
    }

}
