package me.hapyl.fight.game;

import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstraction is used to safely use {@link GamePlayer} without having to check if it's null.
 */
public class AbstractGamePlayer {

    public static final AbstractGamePlayer NULL_GAME_PLAYER = new AbstractGamePlayer();

    /**
     * Returns this player hero that is currently playing.
     *
     * @return this player hero that is currently playing.
     */
    @Nonnull
    public Hero getHero() {
        throw new NullPointerException("must override getHero()");
    }

    /**
     * Returns players status as string, whether they are alive, dead or spectating.
     *
     * @return players status as string, whether they are alive, dead or spectating.
     */
    public String getStatusString() {
        return isDead() ? "&cDead" : isSpectator() ? "&7Spectator" : isRespawning() ? "&eRespawning" : "&aAlive";
    }

    /**
     * Returns true if player has died during the game and currently spectating or waiting for respawn.
     *
     * @return true if player has died during the game and currently spectating or waiting for respawn.
     */
    public boolean isDead() {
        return false;
    }

    /**
     * Returns true if player started the game as spectator.
     *
     * @return true if player started the game as spectator.
     */
    public boolean isSpectator() {
        return false;
    }

    /**
     * Returns true if player is not dead and not spectator.
     *
     * @return true if player is not dead and not spectator.
     */
    public boolean isAlive() {
        return false;
    }

    /**
     * Marks that player has moved.
     */
    public void markLastMoved() {
    }

    /**
     * Returns currently equipped skin for {@link #getHero()} or null if none.
     *
     * @return currently equipped skin for {@link #getHero()} or null if none.
     */
    @Nullable
    public Skins getSkin() {
        return null;
    }

    /**
     * Sets players ultimate point regeneration modifier, default is 1.0 which is 1 point per second.
     *
     * @param d - New modifier.
     */
    public void setUltimateAccelerationModifier(double d) {
    }

    /**
     * Returns a modifier for ultimate points generation.
     *
     * @return a modifier for ultimate points generation.
     */
    public double getUltimateAccelerationModifier() {
        return 1.0d;
    }

    /**
     * Returns millis of the last time player has moved.
     *
     * @return millis of the last time player has moved.
     */
    public long getLastMoved() {
        return -1;
    }

    /**
     * Returns true if player has moved in the last period of time.
     *
     * @param millis - Period of time to check.
     * @return true if player has moved in the last period of time.
     */
    public boolean hasMovedInLast(long millis) {
        return getLastMoved() != 0 && (System.currentTimeMillis() - getLastMoved()) < millis;
    }

    /**
     * Interrupts player's current action
     */
    public void interrupt() {
    }

    /**
     * Heals player for provided amount.
     *
     * @param d - Amount to heal for.
     */
    public void heal(double d) {
    }

    /**
     * Damages player for provided amount.
     *
     * @param d - Amount of damage.
     */
    public void damage(double d) {
    }

    /**
     * Damages player for provided amount with a damage cause.
     *
     * @param d     - Amount of damage.
     * @param cause - Damage cause.
     */
    public void damage(double d, EnumDamageCause cause) {
    }

    /**
     * Damages player for provided amount with a damager.
     *
     * @param d       - Amount of damage.
     * @param damager - Damager.
     */
    public void damage(double d, LivingEntity damager) {
    }

    /**
     * Damages player for provided amount with a damager and damage cause.
     *
     * @param d       - Amount of damage.
     * @param damager - Damager.
     * @param cause   - Damage cause.
     */
    public void damage(double d, LivingEntity damager, EnumDamageCause cause) {
    }

    /**
     * Kills player if health is zero.
     *
     * @param force - Ignore health restriction.
     */
    public void die(boolean force) {
    }

    /**
     * Adds Game Effect to a player.
     *
     * @param type  - Effect type.
     * @param ticks - Duration.
     */
    public void addEffect(GameEffectType type, int ticks) {
    }

    /**
     * Adds Game Effect to a player.
     *
     * @param type     - Effect type.
     * @param ticks    - Duration.
     * @param override - Override duration.
     */
    public void addEffect(GameEffectType type, int ticks, boolean override) {
    }

    /**
     * Returns true if player has Game Effect.
     *
     * @param type - Effect type.
     * @return true if player has Game Effect.
     */
    public boolean hasEffect(GameEffectType type) {
        return false;
    }

    /**
     * Removes Game Effect from a player.
     *
     * @param type - Effect type.
     */
    public void removeEffect(GameEffectType type) {
    }

    /**
     * Adds Potion Effect to a player.
     *
     * @param type      - Effect type.
     * @param duration  - Duration in ticks.
     * @param amplifier - Amplifier of the effect.
     */
    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
    }

    /**
     * Removes players potion effect.
     *
     * @param type - Effect type.
     */
    public void removePotionEffect(PotionEffectType type) {
    }

    /**
     * Adds ultimate points to player.
     *
     * @param i - Point to add.
     */
    public void addUltimatePoints(int i) {
    }

    /**
     * Returns last damage cause or NONE.
     *
     * @return last damage cause or NONE.
     */
    public EnumDamageCause getLastDamageCause() {
        return EnumDamageCause.NONE;
    }

    public void setLastDamageCause(EnumDamageCause cause) {
    }

    /**
     * Returns last entity that damaged player or null.
     *
     * @return last entity that damaged player or null.
     */
    @Nullable
    public LivingEntity getLastDamager() {
        return null;
    }

    /**
     * Sets last damager.
     *
     * @param entity - New last damager.
     */
    public void setLastDamager(LivingEntity entity) {
    }

    /**
     * Sets health of a player.
     *
     * @param d - Health to set.
     */
    public void setHealth(double d) {
    }

    /**
     * Returns current players' health.
     *
     * @return current players' health.
     */
    public double getHealth() {
        return getMaxHealth();
    }

    /**
     * Returns current players' health in integer format.
     *
     * @return current players' health in integer format.
     */
    public String getHealthFormatted() {
        return "abstract";
    }

    /**
     * Returns players' max health.
     *
     * @return players' max health.
     */
    public double getMaxHealth() {
        return 100.0d;
    }

    /**
     * Returns players' min health.
     *
     * Visual health cannot be lower than 0.5, since player will actually die if it is.
     * Technically 🤓, it's 0.1 or something, but system works with 0.5 so imma keep it that way.
     *
     * @return players' min health.
     */
    public double getMinHealth() {
        return 0.5d;
    }

    /**
     * Returns true if player's ultimate is ready, false othewise.
     *
     * @return true if player's ultimate is ready, false othewise.
     */
    public boolean isUltimateReady() {
        return false;
    }

    /**
     * Sends chat message to a player.
     *
     * @param message - Message.
     * @param objects - Formatter.
     */
    public void sendMessage(String message, Object... objects) {
    }

    /**
     * Sends title message to a player.
     *
     * @param title    - Title.
     * @param subtitle - Subtitle.
     * @param fadeIn   - Fade in ticks.
     * @param stay     - Stay ticks.
     * @param fadeOut  - Fade out ticks.
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    }

    /**
     * Sends actionbar message to a player.
     *
     * @param text    - Message.
     * @param objects - Formatter.
     */
    public void sendActionbar(String text, Object... objects) {
    }

    /**
     * Plays a sound to a player.
     *
     * @param sound - Sound to play.
     * @param pitch - Pitch of the sound.
     */
    public void playSound(Sound sound, float pitch) {
    }

    /**
     * Returns player's current stats.
     *
     * @return player's current stats.
     */
    @Nullable
    public StatContainer getStats() {
        return null;
    }

    /**
     * Returns true if player is currently respawning, false otherwise.
     *
     * @return true if player is currently respawning, false otherwise.
     */
    public boolean isRespawning() {
        return false;
    }

    /**
     * Returns game player. <b>Will throw error if called from AbstractGamePlayer!</b>
     *
     * @return game player.
     */
    public GamePlayer getGamePlayer() {
        return (GamePlayer) this;
    }

    /**
     * Returns player current team, not lobby team.
     *
     * @return player current team, not lobby team.
     */
    public GameTeam getTeam() {
        return GameTeam.getPlayerTeam(getGamePlayer());
    }

    /**
     * Returns player's kill streak.
     *
     * @return player's kill streak.
     */
    public int getKillStreak() {
        return 0;
    }

    /**
     * Respawns the player.
     */
    public void respawn() {
    }

    /**
     * Respawns the player after provided duration.
     *
     * @param tick - Duration in ticks.
     */
    public void respawnIn(int tick) {
    }

    /**
     * Sets if player is dead.
     *
     * @param dead - True if dead, false otherwise.
     */
    public void setDead(boolean dead) {
    }

    /**
     * Returns actual player of this game player or throws error if called from AbstractGamePlayer.
     *
     * @return actual player of this game player or throws error if called from AbstractGamePlayer.
     */
    public Player getPlayer() {
        throw new IllegalStateException("must override getPlayer()");
    }

    /**
     * Sends warning message to player's title.
     *
     * @param warning - Warning message.
     * @param stay    - Stay time in ticks.
     */
    public void sendWarning(String warning, int stay) {
        Chat.sendTitle(getPlayer(), "&4&l⚠", warning, 0, stay, 5);
    }

    /**
     * Returns true if this player is abstract (Not real).
     * If false, this player is real.
     *
     * @return True if this game is player (Not real), false otherwise.
     */
    public boolean isAbstract() {
        return true;
    }

    /**
     * Returns string representation of this object.
     *
     * @return string representation of this object.
     */
    @Override
    public String toString() {
        return "AbstractGamePlayer{}";
    }

    public void teleport(Location location) {
        getPlayer().teleport(location);
    }
}
