package me.hapyl.fight.game;

import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AbstractGamePlayer {

    public static final AbstractGamePlayer NULL_GAME_PLAYER = new AbstractGamePlayer();

    // this works as IllegalStateException but without the error
    private void displayError() {
        //Bukkit.getConsoleSender().sendMessage("&4IllegalState! &cCalled null IGamePlayer");
    }

    @Nonnull
    public Hero getHero() {
        throw new NullPointerException("must override getHero()");
    }

    public String getStatusString() {
        return isDead() ? "&cDead" : isSpectator() ? "&7Spectator" : isRespawning() ? "&eRespawning" : "&aAlive";
    }

    public boolean isAbstract() {
        return true;
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
     * Returns a modifier for cooldown acceleration.
     *
     * @return a modifier for cooldown acceleration.
     */
    public double getCooldownAccelerationModifier() {
        return 1.0d;
    }

    public void setCooldownAccelerationModifier(double d) {
    }

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
     * Heals player for provided amount.
     *
     * @param d - Amount to heal for.
     */
    public void heal(double d) {
        displayError();
    }

    /**
     * Damages player for provided amount.
     *
     * @param d - Amount of damage.
     */
    public void damage(double d) {
        displayError();
    }

    /**
     * Damages player for provided amount with a damage cause.
     *
     * @param d     - Amount of damage.
     * @param cause - Damage cause.
     */
    public void damage(double d, EnumDamageCause cause) {
        displayError();
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
        displayError();
    }

    /**
     * Kills player if health is zero.
     *
     * @param force - Ignore health restriction.
     */
    public void die(boolean force) {
        displayError();
    }

    /**
     * Adds Game Effect to a player.
     *
     * @param type  - Effect type.
     * @param ticks - Duration.
     */
    public void addEffect(GameEffectType type, int ticks) {
        displayError();
    }

    /**
     * Adds Game Effect to a player.
     *
     * @param type     - Effect type.
     * @param ticks    - Duration.
     * @param override - Override duration.
     */
    public void addEffect(GameEffectType type, int ticks, boolean override) {
        displayError();
    }

    /**
     * Returns true if player has Game Effect.
     *
     * @param type - Effect type.
     * @return true if player has Game Effect.
     */
    public boolean hasEffect(GameEffectType type) {
        displayError();
        return false;
    }

    /**
     * Removes Game Effect from a player.
     *
     * @param type - Effect type.
     */
    public void removeEffect(GameEffectType type) {
        displayError();
    }

    /**
     * Adds Potion Effect to a player.
     *
     * @param type      - Effect type.
     * @param duration  - Duration in ticks.
     * @param amplifier - Amplifier of the effect.
     */
    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
        displayError();
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
        displayError();
        return EnumDamageCause.NONE;
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
        displayError();
    }

    /**
     * Returns current players' health.
     *
     * @return current players' health.
     */
    public double getHealth() {
        displayError();
        return getMaxHealth();
    }

    /**
     * Returns current players' health in integer format.
     *
     * @return current players' health in integer format.
     */
    public String getHealthFormatted() {
        displayError();
        return "-1";
    }

    /**
     * Returns players' max health.
     *
     * @return players' max health.
     */
    public double getMaxHealth() {
        displayError();
        return 100.d;
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

    @Override
    public String toString() {
        return "AbstractGamePlayer{}";
    }

    public boolean isRespawning() {
        return false;
    }

    public GamePlayer getGamePlayer() {
        return (GamePlayer) this;
    }

    public GameTeam getTeam() {
        return GameTeam.getPlayerTeam(getGamePlayer());
    }

    public void respawn() {
    }

    public void respawnIn(int i) {
    }

    public void setDead(boolean b) {

    }
}
