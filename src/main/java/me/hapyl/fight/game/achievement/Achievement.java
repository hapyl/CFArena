package me.hapyl.fight.game.achievement;

import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.trigger.EntityTrigger;
import me.hapyl.fight.trigger.Subscribe;
import me.hapyl.fight.util.PatternId;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Base achievement class.
 */
public class Achievement extends PatternId {

    private static final SimpleDateFormat COMPLETE_FORMAT = new SimpleDateFormat("MMMM d'th' yyyy, HH:mm:ss z");
    private static final String GRADIENT = new Gradient("ACHIEVEMENT COMPLETE")
            .makeBold()
            .rgb(new Color(235, 100, 52), new Color(235, 232, 52), Interpolators.QUADRATIC_SLOW_TO_FAST);

    private final String name;
    private final String description;

    protected int maxCompleteCount;
    private Category category;
    private int pointReward;

    public Achievement(@Nullable @ForceLowercase String id, @Nonnull String name, @Nonnull String description) {
        super(Pattern.compile("^[a-z0-9_]+$"));
        this.name = name;
        this.description = description;
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;
        this.pointReward = 5;

        if (id != null) {
            setId(id);
        }
    }

    public Achievement(@Nonnull String name, @Nonnull String description) {
        this(null, name, description);
    }

    public int getPointReward() {
        return pointReward;
    }

    @Nonnull
    public String getPointRewardFormatted() {
        return formatPointReward(pointReward);
    }

    @Nonnull
    public String formatPointReward(int points) {
        return me.hapyl.fight.game.color.Color.ROYAL_BLUE.color(points + " " + Currency.ACHIEVEMENT_POINT.getFormatted());
    }

    public Achievement setPointReward(int pointReward) {
        this.pointReward = pointReward;
        return this;
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
        Chat.sendCenterMessage(player, me.hapyl.fight.game.color.Color.DARK_ORANGE + getName());
        Chat.sendMessage(player, "");

        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
    }

    /**
     * Tries to complete achievement for player.
     *
     * @param player - Player to complete achievement for.
     * @return true if success, false if already completed.
     */
    public final boolean complete(Player player) {
        final int completeCount = getCompleteCount(player);

        // If already completed, check if progress achievement
        if (completeCount > 0 && completeCount >= getMaxCompleteCount()) {
            return false;
        }

        return setCompleteCount(player, completeCount + 1);
    }

    public final boolean setCompleteCount(Player player, int completeCount) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final AchievementEntry entry = database.getAchievementEntry();

        entry.setCompleteCount(this, completeCount);

        awardPlayer(player);
        markComplete(player);
        displayComplete(player);
        return true;
    }

    public void awardPlayer(Player player) {
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).getCurrency();

        currency.add(Currency.ACHIEVEMENT_POINT, pointReward);
    }

    public void markComplete(Player player) {
        final AchievementEntry entry = PlayerDatabase.getDatabase(player).getAchievementEntry();

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

        completeAll(team.getPlayersAsPlayers());
    }

    /**
     * Returns true if player has completed this achievement at least once.
     *
     * @param player - Player to check.
     * @return true if a player has completed this achievement at least once.
     */
    public final boolean hasCompletedAtLeastOnce(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().hasCompletedAtLeastOnce(this);
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
        return PlayerDatabase.getDatabase(player).getAchievementEntry().getCompleteCount(this);
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

    /**
     * Sets the trigger for this achievement.
     * Triggers are used to trigger achievement whenever a certain action happens.
     *
     * @param sub     - Subscribe.
     * @param trigger - Trigger.
     */
    public <T extends EntityTrigger> Achievement setTrigger(Subscribe<T> sub, AchievementTrigger<T> trigger) {
        sub.subscribe(t -> {
            final LivingEntity entity = t.entity.getEntity();

            if (!(entity instanceof Player player) || isComplete(player)) { // don't check if already complete
                return;
            }

            if (trigger.test(t)) {
                complete(player);
            }
        });

        return this;
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
        builder.addLore("&b&lREWARD:" + checkmark(isComplete));
        builder.addLore(getPointRewardFormatted());
    }

    @Nonnull
    public String checkmark(boolean condition) {
        return condition ? " &a✔" : " &c❌";
    }

}
