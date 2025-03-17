package me.hapyl.fight.game.experience;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.database.entry.MetadataEntry;
import me.hapyl.fight.event.ProfileInitializationEvent;
import me.hapyl.fight.game.cosmetic.CosmeticRegistry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.reward.HeroUnlockReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardResource;
import me.hapyl.fight.game.reward.StackedReward;
import me.hapyl.fight.npc.CommissionerNPC;
import me.hapyl.fight.npc.TheEyeNPC;
import me.hapyl.fight.registry.Registries;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class Experience extends DependencyInjector<Main> implements Listener {

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

        CF.registerEvents(this);
    }

    @EventHandler
    public void handleProfileInitializationEvent(ProfileInitializationEvent ev) {
        triggerUpdate(ev.getPlayer());
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

    public void updatePlayerRewards(@Nonnull Player player) {
        final ExperienceEntry entry = getDatabaseEntry(player);
        final long playerLevel = entry.get(ExperienceEntry.Type.LEVEL);
        final List<Reward> unclaimedRewards = Lists.newArrayList();

        for (long i = 1; i <= playerLevel; i++) {
            final List<Reward> rewards = getRewards(i);

            rewards.forEach(reward -> {
                if (!reward.hasClaimed(player)) {
                    unclaimedRewards.add(reward);
                }
            });
        }

        if (unclaimedRewards.isEmpty()) {
            return;
        }

        Message.info(player, "");
        Message.success(player, "There were new rewards for your reached level, and the following were credited to you:");
        Message.sound(player, Sound.ENTITY_VILLAGER_AMBIENT, 1.0f);

        new StackedReward(unclaimedRewards).grantAll(player);

        Message.info(player, "");
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

            for (Reward reward : rewards) {
                reward.grant(player, false); // Reward message is sent separately
            }
        }

        // Fix achievement
        Registries.achievements().LEVEL_TIERED.setCompleteCount(player, (int) toLevel);

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
        Chat.sendCenterMessage(player, "&eYou are now level %s!".formatted(level));

        // Display rewards
        final List<Reward> rewards = Lists.newArrayList(getRewards(level));
        rewards.removeIf(reward -> reward.hasClaimed(player));

        if (!rewards.isEmpty()) {
            Reward.sendRewardsHeader(player);
            rewards.forEach(reward -> reward.sendRewardMessage(player));
        }

        Chat.sendMessage(player, "");
    }

    public long getMaxLevel() {
        return ExperienceEntry.Type.LEVEL.getMaxValue();
    }

    @Nonnull
    public List<Reward> getRewards(long level) {
        if (level < 1 || level > getMaxLevel()) {
            return List.of();
        }

        return experienceLevelMap.get(level).getRewards();
    }

    /**
     * Updates player's experience data UI.
     */
    public void triggerUpdate(Player player) {
        updateProgressBar(player);
        updatePlayerRewards(player);
    }

    /**
     * Returns the progress of the player to the next level.
     *
     * @param player - player to get progress for.
     * @return progress from 0 to 1.
     */
    public float getProgress(Player player) {
        return Math.clamp((float) (getExpScaled(player)) / (float) (getExpScaledNext(player)), 0.0f, 1.0f);
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
        return CF.getDatabase(player).experienceEntry;
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
                experienceLevel.addReward(Reward.of(
                        Key.ofString("prestige_color_%s".formatted(color.name().toLowerCase())), "Prestige Color", color + " &7prestige color", player -> {
                        }
                ));
            }

            experienceLevelMap.put(level, experienceLevel);
        }
    }

    private void setupRewards() {
        // Resource rewards
        experienceLevelMap.forEach((lvl, level) -> {
            final Reward reward = new Reward(Key.ofString("level_%s_reward_resource".formatted(lvl)), "Level %s Rewards".formatted(lvl));

            reward.withResource(RewardResource.COINS, 1000 * lvl);

            if (level.isPrestige()) {
                reward.withResource(RewardResource.RUBY, 1);
            }

            level.addReward(reward);
        });

        // Hero unlocks
        for (Hero hero : HeroRegistry.values()) {
            final long minLevel = hero.getMinimumLevel();
            if (minLevel > 0) {
                final ExperienceLevel level = experienceLevelMap.get(minLevel);

                if (level != null) {
                    level.addReward(new HeroUnlockReward(hero));
                }
            }
        }

        // Manual rewards
        // Keep manual rewards last for consistency
        final CosmeticRegistry cosmetics = Registries.cosmetics();

        addReward(1, levelReward(1).withCosmetic(cosmetics.PEACE));
        addReward(2, levelReward(2).withCosmetic(cosmetics.EMERALD_EXPLOSION));

        addReward(
                5, Reward.of(
                        Key.ofString("the_eye_remote_communication"), "The Eye Remote Communication", player -> {
                            MetadataEntry.set(player, TheEyeNPC.HAS_UNLOCKED_REMOTE_GUI, true);
                        }
                )
        );

        addReward(
                10, Reward.of(
                        Key.ofString("commission_access"), "Access to &cCommissions", player -> {
                            CommissionerNPC.HAS_UNLOCKED_COMMISSIONS.set(player, true);
                        }
                )
        );
    }

    private void addReward(int level, @Nonnull Reward reward) {
        final ExperienceLevel exp = experienceLevelMap.get((long) level);

        if (exp == null) {
            throw new IllegalArgumentException("There is not such level as: " + level);
        }

        exp.addReward(reward);
    }

    private void updateProgressBar(Player player) {
        final float progress = getProgress(player);

        player.setLevel((int) getLevel(player));
        player.setExp(progress);
    }

    private static Reward levelReward(int level, String name, Consumer<Player> consumer) {
        return new Reward(Key.ofString("level_%s_reward".formatted(level)), name) {
            @Override
            public void doGrant(@Nonnull Player player) {
                super.doGrant(player);
                consumer.accept(player);
            }
        };
    }

    private static Reward levelReward(int level, Consumer<Player> consumer) {
        return levelReward(level, "Level %s Rewards".formatted(level), consumer);
    }

    private static Reward levelReward(int level) {
        return levelReward(
                level, player -> {
                }
        );
    }


}
