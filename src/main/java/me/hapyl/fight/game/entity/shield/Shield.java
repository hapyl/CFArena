package me.hapyl.fight.game.entity.shield;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Shield {

    private final GamePlayer player;
    private final double maxCapacity;

    protected double capacity;

    public Shield(@Nonnull GamePlayer player, double maxCapacity) {
        this.player = player;
        this.maxCapacity = maxCapacity;
        this.capacity = maxCapacity;
    }

    public void regenerate(double amount) {
        final double regenerateAmount = Math.min(capacity + amount, maxCapacity);
        capacity = regenerateAmount;

        onRegenerate(regenerateAmount);
    }

    @PreprocessingMethod
    public final void takeDamage0(double damage) {
        takeDamage(damage);

        if (capacity > 0.0d) {
            onHit(damage);
        }
    }

    public void takeDamage(double damage) {
        capacity -= damage;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public double getCapacity() {
        return capacity;
    }

    /**
     * Called upon shield regenerating a certain capacity.
     *
     * @param amount - Amount of capacity regenerated.
     */
    @Event
    public void onRegenerate(double amount) {
    }

    /**
     * Called upon shield taking a hit.
     *
     * @param amount - Amount of damage taken.
     */
    @Event
    public void onHit(double amount) {
        // Update UI indicator
        final double absorptionAmount = Numbers.clamp(20 / maxCapacity * capacity, 0, 20);

        player.getPlayer().setAbsorptionAmount(absorptionAmount);
    }

    /**
     * Called upon shield being broken.
     */
    @Event
    public void onBreak() {
    }

    /**
     * Called upon shield being removed, be it because of a new shield or time limit or anything except breaking.
     */
    @Event
    public void onRemove() {
    }

    /**
     * Called upon this shield being applied to a player.
     */
    @Event
    public void onCreate() {
    }

    public final void setShield(@Nonnull GamePlayer player) {
        player.setShield(this);
    }

    public final void setShield(@Nonnull Player player) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            throw new IllegalArgumentException("Game player does not exist for " + player);
        }

        setShield(gamePlayer);
    }

    public final void onCreate0() {
        // No idea why it suddenly doesn't work, the only thing I changed was the version
        player.addPotionEffect(PotionEffectType.ABSORPTION, 10000, 4);

        onCreate();
    }

    public final void onBreak0() {
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        onBreak();
    }
}
