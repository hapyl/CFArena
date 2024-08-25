package me.hapyl.fight.game.achievement;

import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.Keyed;
import me.hapyl.fight.util.ChatUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Objects;

/**
 * Base achievement class.
 */
@AutoRegisteredListener
public class Achievement implements Keyed {

    private static final SimpleDateFormat COMPLETE_FORMAT = new SimpleDateFormat("MMMM d'th' yyyy, HH:mm:ss z");
    private static final String GRADIENT = new Gradient("ACHIEVEMENT COMPLETE")
            .makeBold()
            .rgb(new Color(235, 100, 52), new Color(235, 232, 52), Interpolators.QUADRATIC_SLOW_TO_FAST);

    private static final int DEFAULT_POINT_REWARD = 5;

    private final Key key;
    private final String name;
    private final String description;

    protected int maxCompleteCount;

    private Category category;
    private int pointReward;
    private Hero heroSpecific;

    /**
     * @see #builder(Key)
     */
    Achievement(@Nonnull @ForceLowercase Key key, @Nonnull String name, @Nonnull String description) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;
        this.pointReward = DEFAULT_POINT_REWARD;

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    public int getPointReward() {
        return pointReward;
    }

    public Achievement setPointReward(int pointReward) {
        this.pointReward = pointReward;
        return this;
    }

    @Nonnull
    public String getPointRewardFormatted() {
        return formatPointReward(pointReward);
    }

    @Nonnull
    public String formatPointReward(int points) {
        return me.hapyl.fight.game.color.Color.ROYAL_BLUE.color(points + " " + Currency.ACHIEVEMENT_POINT.getFormatted());
    }

    @Nonnull
    public String getType() {
        return "Achievement";
    }

    /**
     * Gets the achievement category.
     *
     * @return the achievement category.
     */
    @Nonnull
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the achievement name.
     *
     * @return the achievement name.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the achievement description.
     *
     * @return the achievement description.
     */
    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     * Displays the completion for the player.
     * Plugin may override this for custom display.
     *
     * @param player - Player.
     */
    public void displayComplete(Player player) {
        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, GRADIENT);

        CenterChat.sendCenteredClickableMessage(
                player,
                ChatColor.GOLD + getName(),
                getShowTextHoverEvent(),
                LazyEvent.runCommand("/viewachievementgui")
        );

        Chat.sendMessage(player, "");

        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
    }

    @Nonnull
    public HoverEvent getShowTextHoverEvent() {
        return ChatUtils.showText(
                ChatColor.GOLD + getName(),
                ChatColor.DARK_GRAY + getType(),
                "",
                getDescription(),
                "",
                "Reward:",
                getPointRewardFormatted()
        );
    }

    /**
     * Tries to complete achievement for player.
     *
     * @param player - Player to complete achievement for.
     * @return true if success, false if already completed.
     */
    public final boolean complete(@Nonnull Player player) {
        final int completeCount = getCompleteCount(player);

        // If already completed, check if progress achievement
        if (completeCount > 0 && completeCount >= getMaxCompleteCount()) {
            return false;
        }

        return setCompleteCount(player, completeCount + 1);
    }

    public final boolean complete(@Nonnull GamePlayer player) {
        return complete(player.getPlayer());
    }

    public final boolean complete(@Nonnull GameTeam team) {
        team.getBukkitPlayers().forEach(this::complete);
        return true;
    }

    public final boolean addCompleteCount(@Nonnull Player player, int completeCount) {
        return setCompleteCount(player, getCompleteCount(player) + completeCount);
    }

    public final boolean setCompleteCount(@Nonnull Player player, int completeCount) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final AchievementEntry entry = database.achievementEntry;

        entry.setCompleteCount(this, completeCount);

        awardPlayer(player);
        markComplete(player);
        displayComplete(player);
        return true;
    }

    public void awardPlayer(Player player) {
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).currencyEntry;

        currency.add(Currency.ACHIEVEMENT_POINT, pointReward);
    }

    public void markComplete(Player player) {
        final AchievementEntry entry = PlayerDatabase.getDatabase(player).achievementEntry;

        onComplete(player);
        entry.setCompletedAt(this, System.currentTimeMillis());
    }

    /**
     * Tries to complete achievement for all players.
     *
     * @param players - Players to complete achievement for.
     */
    public final void completeAll(@Nonnull Collection<Player> players) {
        players.forEach(this::complete);
    }

    /**
     * Tries to complete achievement for all players in a team.
     *
     * @param team - Team to complete for.
     */
    public final void completeAll(@Nonnull GameTeam team) {
        if (team.isEmpty()) {
            return;
        }

        completeAll(team.getBukkitPlayers());
    }

    /**
     * Returns true if player has completed this achievement at least once.
     *
     * @param player - Player to check.
     * @return true if a player has completed this achievement at least once.
     */
    public final boolean hasCompletedAtLeastOnce(@Nonnull Player player) {
        return PlayerDatabase.getDatabase(player).achievementEntry.hasCompletedAtLeastOnce(this);
    }

    public final boolean hasCompletedAtLeastOnce(@Nonnull GamePlayer player) {
        return hasCompletedAtLeastOnce(player.getPlayer());
    }

    /**
     * Returns true if player has completed this achievement the max amount of times.
     *
     * @param player - Player to check.
     * @return true, if a player has completed this achievement the max amount of times.
     */
    public final boolean isComplete(Player player) {
        return getCompleteCount(player) == maxCompleteCount;
    }

    /**
     * Gets the number of times a player has completed this achievement.
     *
     * @param player - The player.
     * @return the number of times a player has completed this achievement.
     */
    public final int getCompleteCount(Player player) {
        return PlayerDatabase.getDatabase(player).achievementEntry.getCompleteCount(this);
    }

    /**
     * Gets the millis at when a player has completed this achievement.
     * Defaults to 0.
     *
     * @param player - Player.
     * @return the millis at when a player has completed this achievement.
     */
    public final long getCompletedAt(Player player) {
        return PlayerDatabase.getDatabase(player).achievementEntry.getCompletedAt(this);
    }

    /**
     * Returns true if this achievement is hidden.
     */
    public boolean isHidden() {
        return this instanceof HiddenAchievement;
    }

    /**
     * Returns true if this achievement can be completed multiple times.
     */
    public boolean isProgressive() {
        return this instanceof TieredAchievement;
    }

    /**
     * Triggers each time player completes this achievement.
     *
     * @param player - Player.
     */
    public void onComplete(Player player) {
    }

    /**
     * Gets the maximum complete count for this achievement.
     *
     * @return the maximum complete count for this achievement.
     */
    public int getMaxCompleteCount() {
        return maxCompleteCount;
    }

    @Nonnull
    public String getCompletedAtFormatted(Player player) {
        return COMPLETE_FORMAT.format(getCompletedAt(player));
    }

    public void format(Player player, ItemBuilder builder) {
        final boolean isComplete = isComplete(player);

        builder.setName((isComplete ? me.hapyl.fight.game.color.Color.SUCCESS : me.hapyl.fight.game.color.Color.ERROR) + getName());
        builder.addLore(me.hapyl.fight.game.color.Color.WARM_GRAY + getType());
        builder.addLore();
        builder.addSmartLore(getDescription());
        builder.addLore();
        builder.addLore("&b&lREWARD:" + BukkitUtils.checkmark(isComplete));
        builder.addLore(getPointRewardFormatted());
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    @Nonnull
    public static Builder builder(@Nonnull Key key) {
        return new Builder(key);
    }

    public static class Builder implements me.hapyl.fight.util.Builder<Achievement> {

        private final Key key;

        private String name;
        private String description;
        private boolean isSecret;
        private int pointReward;
        private Category category;
        private Hero hero;

        private Builder(Key key) {
            this.key = key;
            this.name = "Unnamed achievement.";
            this.description = "No description.";
        }

        public Builder setName(@Nonnull String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(@Nonnull String description) {
            this.description = description;
            return this;
        }

        public Builder setSecret(boolean isSecret) {
            this.isSecret = isSecret;
            return this;
        }

        public Builder setPointReward(int pointReward) {
            this.pointReward = pointReward;
            return this;
        }

        public Builder setCategory(@Nonnull Category category) {
            this.category = category;
            return this;
        }

        public Builder setHeroSpecific(@Nonnull Hero hero) {
            this.hero = hero;
            return this;
        }

        @Nonnull
        @Override
        public Achievement build() {
            final Achievement achievement = isSecret ? new HiddenAchievement(key, name, description) : new Achievement(key, name, description);

            achievement.category = category;
            achievement.pointReward = pointReward;
            achievement.heroSpecific = hero;

            return achievement;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Achievement that = (Achievement) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
