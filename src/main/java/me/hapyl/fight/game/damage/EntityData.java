package me.hapyl.fight.game.damage;

import com.google.common.collect.Maps;
import me.hapyl.fight.annotate.Important;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Stores entity data per-game instance.
 * Used to store custom damage, custom effects, etc.
 */
public final class EntityData {

    public static final EntityData EMPTY = new EntityData(Entities.PIG.spawn(BukkitUtils.defLocation(0, -64, 0), Entity::remove));

    private final Map<GameEffectType, ActiveGameEffect> gameEffects;
    private final Map<Player, Double> damageTaken;

    private final LivingEntity entity;

    @Nullable private LivingEntity lastDamager;
    @Nullable private EnumDamageCause lastDamageCause;

    private double lastDamage;
    private boolean isCrit;

    @Important("Notifies the event that the damage is custom, not vanilla.")
    private boolean wasHit;

    public EntityData(LivingEntity entity) {
        this.entity = entity;
        this.damageTaken = Maps.newHashMap();
        this.gameEffects = Maps.newConcurrentMap();
    }

    /**
     * Returns true if current damage instance is executed used EntityData.
     *
     * @return true if current damage instance is executed used EntityData.
     */
    public boolean isCustomDamage() {
        return wasHit;
    }

    /**
     * Returns true if current damage instance is native.
     *
     * @return true if current damage instance is native.
     */
    public boolean isNativeDamage() {
        return !wasHit;
    }

    /**
     * Returns the last damage taken. Defaults to 0.
     *
     * @return the last damage taken.
     */
    public double getLastDamage() {
        return lastDamage;
    }

    /**
     * Sets the last damage taken.
     *
     * @param lastDamage - Last damage.
     */
    public void setLastDamage(double lastDamage) {
        this.lastDamage = lastDamage;
    }

    /**
     * Gets the actual map of the game effects.
     *
     * @return game effect map.
     */
    public Map<GameEffectType, ActiveGameEffect> getGameEffects() {
        return gameEffects;
    }

    /**
     * Gets the actual map of damage taken.
     *
     * @return damage taken map.
     */
    public Map<Player, Double> getDamageTaken() {
        return damageTaken;
    }

    /**
     * Return true if the last damage taken was critical.
     *
     * @return true if the last damage taken was critical.
     */
    public boolean isCrit() {
        return isCrit;
    }

    /**
     * Sets if the last damage taken was critical.
     *
     * @param crit - Is critical.
     */
    public void setCrit(boolean crit) {
        isCrit = crit;
    }

    /**
     * Gets the cause of the last taken damage.
     *
     * @return the last cause of the taken damage.
     */
    @Nullable
    public EnumDamageCause getLastDamageCause() {
        return lastDamageCause;
    }

    /**
     * Sets the cause of the last taken damage.
     *
     * @param cause - Cause.
     */
    public void setLastDamageCause(@Nullable EnumDamageCause cause) {
        this.lastDamageCause = cause;
    }

    /**
     * Gets the cause of the last taken damage, defaults to {@link EnumDamageCause#ENTITY_ATTACK}.
     *
     * @return the cause of the last taken damage, defaults to {@link EnumDamageCause#ENTITY_ATTACK}.
     */
    @Nonnull
    public EnumDamageCause getLastDamageCauseNonNull() {
        return lastDamageCause == null ? EnumDamageCause.ENTITY_ATTACK : lastDamageCause;
    }

    /**
     * Gets owning entity of this data.
     *
     * @return entity that owns this data.
     */
    @Nonnull
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Gets the last damager.
     *
     * @return the last damager.
     */
    @Nullable
    public LivingEntity getLastDamager() {
        return lastDamager;
    }

    /**
     * Sets the last damager.
     *
     * @param lastDamager - New last damager.
     */
    public void setLastDamager(@Nullable LivingEntity lastDamager) {
        this.lastDamager = lastDamager;
    }

    /**
     * Roots to the actual entity damager.
     */
    @Nullable
    public LivingEntity rootLastDamager() {
        if (lastDamager == null) {
            return null;
        }

        if (lastDamager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity living) {
                return living;
            }

            return null;
        }

        return lastDamager;
    }

    /**
     * Gets the last damager cast to the type, or null if incompatible type.
     *
     * @param cast - To cast.
     * @param <T>  - Type of the damager.
     * @return the last damager cast to the type, or null if incompatible type.
     */
    @Nullable
    public <T> T getLastDamager(Class<T> cast) {
        final LivingEntity living = rootLastDamager();

        if (cast.isInstance(living)) {
            return cast.cast(living);
        }

        return null;
    }

    /**
     * Sets the last damage cause if the last damage was native.
     *
     * @param cause - Bukkit cause.
     */
    public void setLastDamageCauseIfNative(EntityDamageEvent.DamageCause cause) {
        if (isNativeDamage()) {
            lastDamageCause = EnumDamageCause.getFromCause(cause);
        }
    }

    /**
     * Sets the last damager if the last damage was native.
     *
     * @param living - New damager.
     */
    public void setLastDamagerIfNative(LivingEntity living) {
        if (isNativeDamage()) {
            lastDamager = living;
        }
    }

    /**
     * Adds effect to this entity.
     *
     * @param type     - Effect type.
     * @param ticks    - Duration.
     * @param override - True to override existing effect.
     *                 When overriding, the previous effect will be
     *                 overridden, else the duration will be added
     *                 to remaining ticks.
     */
    public void addEffect(GameEffectType type, int ticks, boolean override) {
        final ActiveGameEffect effect = gameEffects.get(type);

        if (effect != null) {
            effect.triggerUpdate();
            if (override) {
                effect.setRemainingTicks(ticks);
            }
            else {
                effect.addRemainingTicks(ticks);
            }
        }
        else {
            gameEffects.put(type, new ActiveGameEffect(entity, type, ticks));
        }
    }

    /**
     * Clears the effect.
     * <b>Note that this does not call onStop()</b>
     *
     * @param type - Type.
     */
    public void clearEffect(GameEffectType type) {
        gameEffects.remove(type);
    }

    /**
     * Removes the effect from this entity.
     *
     * @param type - Type.
     */
    public void removeEffect(GameEffectType type) {
        final ActiveGameEffect gameEffect = gameEffects.get(type);
        if (gameEffect != null) {
            gameEffect.forceStop();
        }
    }

    /**
     * Returns true if this entity has the given effect.
     *
     * @param type - Type.
     * @return true if this entity has the given effect.
     */
    public boolean hasEffect(GameEffectType type) {
        return gameEffects.containsKey(type);
    }

    /**
     * Clears all the effects.
     * <b>Note that this does not call onStop()</b>
     */
    public void clearEffects() {
        this.gameEffects.clear();
    }

    /**
     * Resets all the damage data, such as:
     * <ul>
     *     <li>{@link #lastDamager}</li>
     *     <li>{@link #lastDamageCause}</li>
     *     <li>{@link #lastDamage}</li>
     *     <li>{@link #isCrit}</li>
     * </ul>
     */
    public void resetDamage() {
        lastDamager = null;
        lastDamageCause = null;
        lastDamage = 0.0d;
        isCrit = false;
    }

    /**
     * Notifiers player about incoming damage for this data.
     *
     * @param player - Player to notify.
     */
    public void notifyChatIncoming(Player player) {
        final double damage = this.lastDamage;

        if (damage < 1) {
            return;
        }

        final String prefix = "&7[&c⚔&7] &f";
        String message = "&l%.2f &ffrom &l%s".formatted(damage, Chat.capitalize(this.getLastDamageCauseNonNull()));

        final LivingEntity lastDamager = this.rootLastDamager();

        if (lastDamager != null) {
            message += " &fby &l" + lastDamager.getName();
        }

        if (this.isCrit) {
            message += " &b&lCRITICAL";
        }

        Chat.sendMessage(player, prefix + message);
    }

    /**
     * Notifiers player about outgoing damage for this data.
     *
     * @param player - Player to notify.
     */
    public void notifyChatOutgoing(Player player) {
        final double damage = this.lastDamage;

        if (damage < 1) {
            return;
        }

        final String prefix = "&7[&a⚔&7] &f";

        Chat.sendMessage(
                player,
                prefix + "&l%.2f &fusing &l%s &fto &l%s%s".formatted(
                        damage,
                        Chat.capitalize(this.getLastDamageCauseNonNull()),
                        this.entity.getName(),
                        this.isCrit ? " &b&lCRITICAL" : ""
                )
        );
    }

    @Override
    public String toString() {
        return "EntityData{" +
                "entity=" + entity.getName() +
                ", lastDamager=" + lastDamager +
                ", lastDamageCause=" + lastDamageCause +
                ", lastDamage=" + lastDamage +
                ", isCrit=" + isCrit +
                ", wasHit=" + wasHit +
                '}';
    }

    // static members

    /**
     * Performs damage, with a given no damage ticks.
     *
     * @param entity  - Entity to damage.
     * @param damage  - Damage.
     * @param damager - Damager.
     * @param cause   - Cause.
     * @param tick    - No damage ticks.
     */
    @Super
    public static void damageTick(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, int tick) {
        final int maximumNoDamageTicks = entity.getMaximumNoDamageTicks();
        tick = Numbers.clamp(tick, 0, maximumNoDamageTicks);

        entity.setMaximumNoDamageTicks(tick);
        damage(entity, damage, damager, cause == null ? EnumDamageCause.ENTITY_ATTACK : cause);
        entity.setMaximumNoDamageTicks(maximumNoDamageTicks);
    }

    // see @Super method
    public static void damageTick(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, int tick) {
        damageTick(entity, damage, damager, null, tick);
    }

    // see @Super method
    public static void damageTick(@Nonnull LivingEntity entity, double damage, int tick) {
        damageTick(entity, damage, null, null, tick);
    }

    /**
     * Performs damage to an entity.
     *
     * @param entity  - Entity to damage.
     * @param damage  - Damage.
     * @param damager - Damager.
     * @param cause   - Cause.
     */
    @Super
    public static void damage(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        final EntityData data = getEntityData(entity);

        // Don't reassign the damage if self damage!
        // That's the whole point of the system to
        // award the last damager even if player killed themselves.
        if (damager != null && entity != damager) {
            data.lastDamager = damager;
        }

        if (cause != null) {
            data.lastDamageCause = cause;
        }

        data.lastDamage = damage;

        // Call the damage event
        data.wasHit = true; // This tag is VERY important for calculations
        entity.damage(damage, damager);
        data.wasHit = false;
    }

    // see @Super method
    public static void damage(@Nonnull LivingEntity entity, double damage, @Nullable LivingEntity damager) {
        damage(entity, damage, damager, null);
    }

    // see @Super method
    public static void damage(@Nonnull LivingEntity entity, double damage) {
        damage(entity, damage, null, null);
    }

    /**
     * Damages all entities in the AoE.
     *
     * @param location  - Center of the AoE damage.
     * @param radius    - Radius of AoE damage.
     * @param damage    - Damage.
     * @param damager   - Damager
     * @param cause     - Cause.
     * @param predicate - Predicate for the entities.
     * @return list of damaged entities.
     */
    public static List<LivingEntity> damageAoE(@Nonnull Location location, double radius, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingEntity> predicate) {
        final List<LivingEntity> entities = Utils.getEntitiesInRangeValidateRange(location, radius).stream().filter(predicate).toList();

        for (LivingEntity entity : entities) {
            damage(entity, damage, damager, cause);
        }

        return entities;
    }

    /**
     * Gets the entity data for the given entity from the current game instance.
     *
     * @param entity - Entity.
     * @return the entity data for the given entity from the current game instance.
     */
    @Nonnull
    public static EntityData getEntityData(@Nonnull LivingEntity entity) {
        return Manager.current().getCurrentGame().getEntityData(entity);
    }

    /**
     * Reset damage data for every entity in the current game instance.
     */
    public static void resetDamageData() {
        final IGameInstance instance = Manager.current().getCurrentGame();

        if (instance instanceof GameInstance gameInstance) {
            gameInstance.getEntityData().forEach((entity, data) -> {
                data.resetDamage();
            });
        }
    }
}
