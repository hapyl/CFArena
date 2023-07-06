package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.attribute.PlayerAttributes;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentQueue;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Whenever manager requests a GamePlayer, it will
 * return either a valid GamePlayer, or {@link #NULL_GAME_PLAYER}.
 * <p>
 * Null game player is an empty GamePlayer base.
 * <p>
 * In reality, if {@link #NULL_GAME_PLAYER} is returned,
 * the developer is doing something wrong. But it's better
 * than catching a null pointer.
 */
public interface IGamePlayer {

    /**
     * Default GamePlayer if failed to retrieve player.
     * Should never happen unless unsafe call was made.
     */
    IGamePlayer NULL_GAME_PLAYER = new NullGamePlayer();

    /**
     * Returns this player hero that is currently playing.
     *
     * @return this player hero that is currently playing.
     */
    @Nonnull
    Hero getHero();

    /**
     * Returns players status as string, whether they are alive, dead or spectating.
     *
     * @return players status as string, whether they are alive, dead or spectating.
     */
    default String getStatusString() {
        return isDead() ? "&cDead" : isSpectator() ? "&7Spectator" : isRespawning() ? "&eRespawning" : "&aAlive";
    }

    /**
     * Returns true if player has died during the game and currently spectating or waiting for respawn.
     *
     * @return true if player has died during the game and currently spectating or waiting for respawn.
     */
    boolean isDead();

    /**
     * Sets if player is dead.
     *
     * @param dead - True if dead, false otherwise.
     */
    void setDead(boolean dead);

    /**
     * Returns true if player can move; false otherwise.
     *
     * @return true if a player can move; false otherwise.
     */
    boolean canMove();

    /**
     * Sets if player can move.
     */
    void setCanMove(boolean canMove);

    /**
     * Returns player's combat tag duration, or 0 if not in combat.
     *
     * @return player's combat tag duration or 0 if not in combat.
     */
    long getCombatTag();

    /**
     * Mark player as combat tagged.
     */
    void markCombatTag();

    /**
     * Returns true if player is in combat; false otherwise.
     *
     * @return true if a player is in combat; false otherwise.
     */
    boolean isCombatTagged();

    /**
     * Gets the player attributes.
     *
     * @return player attributes.
     */
    @Nonnull
    PlayerAttributes getAttributes();

    /**
     * Returns player's talent queue.
     *
     * @return player's talent queue.
     * @see TalentQueue
     */
    @Nonnull
    TalentQueue getTalentQueue();

    /**
     * Returns player's current input talent, if any; null otherwise.
     *
     * @return player's current input talent, if any; null otherwise.
     */
    @Nullable
    InputTalent getInputTalent();

    /**
     * Sets player's current input talent; null to remove.
     *
     * @param inputTalent - Input talent to set.
     */
    void setInputTalent(@Nullable InputTalent inputTalent);

    /**
     * Returns true if player's current input talent is not null.
     *
     * @return true if player's current input talent is not null.
     */
    default boolean hasInputTalent() {
        return getInputTalent() != null;
    }

    /**
     * Returns true if player started the game as spectator.
     *
     * @return true if player started the game as spectator.
     */
    boolean isSpectator();

    /**
     * Returns true if player is not dead and not spectator.
     *
     * @return true if player is not dead and not spectator.
     */
    boolean isAlive();

    /**
     * Marks that player has moved.
     */
    void markLastMoved();

    /**
     * Returns a modifier for ultimate points generation.
     *
     * @return a modifier for ultimate points generation.
     */
    double getUltimateAccelerationModifier();

    /**
     * Sets players ultimate point regeneration modifier, default is 1.0 which is 1 point per b.
     *
     * @param d - New modifier.
     */
    void setUltimateAccelerationModifier(double d);

    /**
     * Returns millis of the last time player has moved.
     *
     * @return millis of the last time player has moved.
     */
    long getLastMoved();

    /**
     * Returns true if player has moved in the last period of time.
     *
     * @param millis - Period of time to check.
     * @return true if player has moved in the last period of time.
     */
    default boolean hasMovedInLast(long millis) {
        return getLastMoved() != 0 && (System.currentTimeMillis() - getLastMoved()) < millis;
    }

    /**
     * Interrupts player's current action
     */
    void interrupt();

    /**
     * Heals player for provided amount.
     *
     * @param d - Amount to heal for.
     */
    void heal(double d);

    /**
     * Damages player for provided amount.
     *
     * @param d - Amount of damage.
     */
    void damage(double d);

    /**
     * Damages player for provided amount with a damage cause.
     *
     * @param d     - Amount of damage.
     * @param cause - Damage cause.
     */
    void damage(double d, EnumDamageCause cause);

    /**
     * Damages player for provided amount with a damager.
     *
     * @param d       - Amount of damage.
     * @param damager - Damager.
     */
    void damage(double d, LivingEntity damager);

    /**
     * Damages player for provided amount with a damager and damage cause.
     *
     * @param d       - Amount of damage.
     * @param damager - Damager.
     * @param cause   - Damage cause.
     */
    void damage(double d, LivingEntity damager, EnumDamageCause cause);

    /**
     * Kills player if health is zero.
     *
     * @param force - Ignore health restriction.
     */
    void die(boolean force);

    /**
     * Adds Game Effect to a player.
     *
     * @param type  - Effect type.
     * @param ticks - Duration.
     */
    void addEffect(GameEffectType type, int ticks);

    /**
     * Adds Game Effect to a player.
     *
     * @param type     - Effect type.
     * @param ticks    - Duration.
     * @param override - Override duration.
     */
    void addEffect(GameEffectType type, int ticks, boolean override);

    /**
     * Returns true if player has Game Effect.
     *
     * @param type - Effect type.
     * @return true if player has Game Effect.
     */
    boolean hasEffect(GameEffectType type);

    /**
     * Removes Game Effect from a player.
     *
     * @param type - Effect type.
     */
    void removeEffect(GameEffectType type);

    /**
     * Adds Potion Effect to a player.
     *
     * @param type      - Effect type.
     * @param duration  - Duration in ticks.
     * @param amplifier - Amplifier of the effect.
     */
    void addPotionEffect(PotionEffectType type, int duration, int amplifier);

    /**
     * Removes players potion effect.
     *
     * @param type - Effect type.
     */
    void removePotionEffect(PotionEffectType type);

    /**
     * Adds ultimate points to player.
     *
     * @param i - Point to add.
     */
    void addUltimatePoints(int i);

    /**
     * Returns last damage cause or NONE.
     *
     * @return last damage cause or NONE.
     */
    @Nonnull
    EnumDamageCause getLastDamageCause();

    void setLastDamageCause(EnumDamageCause cause);

    /**
     * Returns last entity that damaged player or null.
     *
     * @return last entity that damaged player or null.
     */
    @Nullable
    LivingEntity getLastDamager();

    /**
     * Sets last damager.
     *
     * @param entity - New last damager.
     */
    void setLastDamager(LivingEntity entity);

    /**
     * Returns current players' health.
     *
     * @return current players' health.
     */
    double getHealth();

    /**
     * Sets health of a player.
     *
     * @param d - Health to set.
     */
    void setHealth(double d);

    /**
     * Returns current players' health in integer format.
     *
     * @return current players' health in integer format.
     */
    String getHealthFormatted();

    /**
     * Returns players' max health.
     *
     * @return players' max health.
     */
    double getMaxHealth();

    /**
     * Returns players' min health.
     * <p>
     * Visual health cannot be lower than 0.5, since player will actually die if it is.
     * Technically 🤓, it's 0.1 or something, but system works with 0.5 so imma keep it that way.
     *
     * @return players' min health.
     */
    double getMinHealth();

    /**
     * Returns true if player's ultimate is ready, false othewise.
     *
     * @return true if player's ultimate is ready, false othewise.
     */
    boolean isUltimateReady();

    /**
     * Sends a chat message to a player.
     *
     * @param message - Message.
     * @param objects - Formatter.
     */
    void sendMessage(String message, Object... objects);

    /**
     * Sends title message to a player.
     *
     * @param title    - Title.
     * @param subtitle - Subtitle.
     * @param fadeIn   - Fade in ticks.
     * @param stay     - Stay ticks.
     * @param fadeOut  - Fade out ticks.
     */
    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Sends actionbar message to a player.
     *
     * @param text    - Message.
     * @param objects - Formatter.
     */
    void sendActionbar(String text, Object... objects);

    /**
     * Plays a sound to a player.
     *
     * @param sound - Sound to play.
     * @param pitch - Pitch of the sound.
     */
    void playSound(Sound sound, float pitch);

    /**
     * Returns player's current stats.
     *
     * @return player's current stats.
     */
    @Nullable
    StatContainer getStats();

    /**
     * Returns true if player is currently respawning, false otherwise.
     *
     * @return true if player is currently respawning, false otherwise.
     */
    boolean isRespawning();

    /**
     * Returns game player. <b>Will throw error if called from AbstractGamePlayer!</b>
     *
     * @return game player.
     */
    default GamePlayer getGamePlayer() {
        return (GamePlayer) this;
    }

    /**
     * Returns player current team, not lobby team.
     *
     * @return player current team, not lobby team.
     */
    default GameTeam getTeam() {
        return GameTeam.getPlayerTeam(getGamePlayer());
    }

    /**
     * Returns player's kill streak.
     *
     * @return player's kill streak.
     */
    int getKillStreak();

    /**
     * Respawns the player.
     */
    void respawn();

    /**
     * Respawns the player after provided duration.
     *
     * @param tick - Duration in ticks.
     */
    void respawnIn(int tick);

    /**
     * Returns actual player of this game player or throws error if called from AbstractGamePlayer.
     *
     * @return actual player of this game player or throws error if called from AbstractGamePlayer.
     */
    Player getPlayer();

    /**
     * Sends warning message to player's title.
     *
     * @param warning - Warning message.
     * @param stay    - Stay time in ticks.
     */
    default void sendWarning(String warning, int stay) {
        Chat.sendTitle(getPlayer(), "&4&l⚠", warning, 0, stay, 5);
    }

    /**
     * Returns true if this is a real GamePlayer instance, false otherwise.
     *
     * @return true if this is a real GamePlayer instance, false otherwise.
     */
    boolean isReal();

    /**
     * Teleports player to the given location.
     *
     * @param location - Location to teleport to.
     */
    default void teleport(Location location) {
        getPlayer().teleport(location);
    }

    /**
     * Returns UUID of this owner.
     *
     * @return UUID of this owner.
     */
    @Nonnull
    default UUID getUUID() {
        final Player player = getPlayer();
        if (player == null) {
            return new UUID(0, 0);
        }

        return player.getUniqueId();
    }

    default double getCooldownModifier() {
        return 1.0d;
    }

    default Map<GameEffectType, ActiveGameEffect> getActiveEffects() {
        return Maps.newHashMap();
    }
}
