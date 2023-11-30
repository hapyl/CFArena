package me.hapyl.fight.game.entity.shield;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Shield {

    private final GamePlayer gamePlayer;
    private final double maxCapacity;

    protected double capacity;

    public Shield(@Nonnull GamePlayer player, double maxCapacity) {
        this.gamePlayer = player;
        this.maxCapacity = maxCapacity;
        this.capacity = maxCapacity;
    }

    public void regenerate(double amount) {
        final double regenerateAmount = Math.min(capacity + amount, maxCapacity);
        capacity = regenerateAmount;

        onRegenerate(regenerateAmount);
    }

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
    public GamePlayer getGamePlayer() {
        return gamePlayer;
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
        gamePlayer.getPlayer().setAbsorptionAmount(Numbers.clamp(20 / maxCapacity * capacity, 0, 20));
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

}
