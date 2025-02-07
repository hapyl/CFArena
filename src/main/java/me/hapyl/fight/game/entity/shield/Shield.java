package me.hapyl.fight.game.entity.shield;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.AscendingDisplay;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Shield implements Ticking {

    public static final double DEFAULT_SHIELD_STRENGTH = 1.0d;
    public static final double INFINITE_SHIELD = Integer.MAX_VALUE;
    public static final String SHIELD_FORMAT = "&e&l%.0f &e🛡";

    protected final GamePlayer player;
    protected final double maxCapacity;
    protected final double shieldStrength;

    protected double capacity;

    public Shield(@Nonnull GamePlayer player, double maxCapacity) {
        this(player, maxCapacity, maxCapacity);
    }

    public Shield(@Nonnull GamePlayer player, double maxCapacity, double initialCapacity) {
        this(player, maxCapacity, initialCapacity, DEFAULT_SHIELD_STRENGTH);
    }

    public Shield(@Nonnull GamePlayer player, double maxCapacity, double initialCapacity, @Range(from = 0, to = 1) double strength) {
        this.player = player;
        this.maxCapacity = maxCapacity;
        this.capacity = initialCapacity;
        this.shieldStrength = strength;
    }

    public double shieldStrength() {
        return shieldStrength;
    }

    /**
     * Returns true if this shield can shield form the given cause.
     * <br>
     * By default, shields protect from any damage but {@link EnumDamageCause#FALL}.
     *
     * @param cause - Cause.
     * @return true if this shield can shield from the given cause; false otherwise.
     */
    public boolean canShield(@Nullable EnumDamageCause cause) {
        if (cause == null) {
            return true;
        }

        if (cause.hasFlag(DamageFlag.PIERCING_DAMAGE) || cause == EnumDamageCause.FALL) {
            return false;
        }

        return true;
    }

    public void regenerate(double amount) {
        final double regenerateAmount = Math.min(capacity + amount, maxCapacity);
        capacity = regenerateAmount;

        onRegenerate(regenerateAmount);
        updateShield();
    }

    @PreprocessingMethod
    public final double takeDamage0(double damage, @Nonnull DamageInstance instance) {
        takeDamage(damage);

        // Only call onHit if the shield is still active after hitting it
        if (capacity > 0.0d) {
            onHit(damage, instance.getDamager());
        }

        return capacity;
    }

    public void takeDamage(double damage) {
        // Check for infinite shield
        if (capacity >= INFINITE_SHIELD) {
            return;
        }

        capacity -= damage;
    }

    public double getCapacity() {
        return capacity;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Called upon shield regenerating a certain capacity.
     *
     * @param amount - Amount of capacity regenerated.
     */
    @EventLike
    public void onRegenerate(double amount) {
    }

    /**
     * Called upon shield taking a hit.
     *
     * @param amount   - Amount of damage taken.
     * @param damager - The entity who dealt the damage to the shield. {@code null} if self-damage.
     */
    @EventLike
    public void onHit(double amount, @Nullable LivingGameEntity damager) {
        updateShield();
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
    @EventLike
    public void onRemove() {
    }

    /**
     * Called upon this shield being applied to a player.
     */
    @EventLike
    public void onCreate() {
    }

    public final void setShield(@Nonnull LivingGameEntity entity) {
        entity.setShield(this);
    }

    public final void onCreate0() {
        // No idea why it suddenly doesn't work, the only thing I changed was the version
        player.addPotionEffect(PotionEffectType.ABSORPTION, 4, 10000);

        updateShield();
        onCreate();
    }

    public final void onBreak0() {
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        onBreak();
    }

    public final void onRemove0() {
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        onRemove();
    }

    public void display(double damage, @Nonnull Location location) {
        new AscendingDisplay("&e🛡 &6%.0f".formatted(damage), 20).display(player.getEyeLocation());
    }

    @Override
    public void tick() {
    }

    public boolean isInfiniteShield() {
        return this.capacity >= INFINITE_SHIELD;
    }

    @Nonnull
    public String getCapacityFormatted() {
        return isInfiniteShield() ? "&e∞ &e🛡" : SHIELD_FORMAT.formatted(this.capacity);
    }

    private void updateShield() {
        // Update UI indicator
        final double absorptionAmount = Math.clamp(20 / maxCapacity * capacity, 0, 20);

        player.getPlayer().setAbsorptionAmount(absorptionAmount);
    }
}
