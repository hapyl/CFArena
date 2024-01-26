package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.Important;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.dot.DotInstance;
import me.hapyl.fight.game.dot.DotInstanceList;
import me.hapyl.fight.game.dot.DamageOverTime;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Used to store custom damage, effects, etc.
 */
public final class EntityData {

    private final Map<Effects, ActiveGameEffect> gameEffects;
    private final Map<DamageOverTime, DotInstanceList> dotMap;
    private final Map<Player, Double> damageTaken;

    private final LivingGameEntity entity;
    @Important(why = "Notifies the event that the damage is custom, not vanilla.")
    boolean wasHit;
    @Nullable private GameEntity lastDamager;
    @Nullable private EnumDamageCause lastDamageCause;
    private double lastDamage;
    private boolean isCrit;

    public EntityData(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.damageTaken = Maps.newHashMap();
        this.gameEffects = Maps.newConcurrentMap();
        this.dotMap = Maps.newConcurrentMap();
    }

    @Nonnull
    public Map<DamageOverTime, DotInstanceList> getDotMap() {
        return dotMap;
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
    @Nonnull
    public Map<Effects, ActiveGameEffect> getGameEffects() {
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
     * Sets the cause of the last-taken damage.
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
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * Gets the last damager.
     *
     * @return the last damager.
     */
    @Nullable
    public GameEntity getLastDamager() {
        return lastDamager;
    }

    /**
     * Sets the last damager.
     *
     * @param lastDamager - New last damager.
     */
    public void setLastDamager(@Nullable GameEntity lastDamager) {
        this.lastDamager = lastDamager;
    }

    /**
     * Gets the last damage as living damager if it's not null and is a living game entity.
     *
     * @return last damager or null.
     */
    @Nullable
    public LivingGameEntity getLastDamagerAsLiving() {
        if (lastDamager instanceof LivingGameEntity living) {
            return living;
        }

        return null;
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

        return lastDamager.getEntity();
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
    public void setLastDamagerIfNative(LivingGameEntity living) {
        if (isNativeDamage()) {
            lastDamager = living;
        }
    }

    /**
     * Adds effect to this entity.
     *
     * @param type      - Effect type.
     * @param amplifier - Amplifier.
     * @param duration  - Duration. {@code -1} for infinite duration.
     * @param override  - True to override existing effect.
     *                  When overriding, the previous effect will be
     *                  overridden, else the duration will be added
     *                  to remaining ticks.
     */
    public void addEffect(@Nonnull Effects type, int amplifier, int duration, boolean override) {
        // Check for effect resistance
        if (type.getEffect().getType() == EffectType.NEGATIVE && entity.hasEffectResistanceAndNotify()) {
            return;
        }

        final ActiveGameEffect effect = gameEffects.get(type);

        if (effect != null) {
            effect.triggerUpdate();

            if (override) {
                effect.setRemainingTicks(duration);
            }
            else {
                effect.addRemainingTicks(duration);
            }
        }
        else {
            gameEffects.put(type, new ActiveGameEffect(entity, type, amplifier, duration));
        }
    }

    /**
     * Clears the effect.
     * <b>Note that this does not call onStop()</b>
     *
     * @param type - Type.
     */
    public void clearEffect(Effects type) {
        gameEffects.remove(type);
    }

    /**
     * Removes the effect from this entity.
     *
     * @param type - Type.
     */
    public void removeEffect(Effects type) {
        final ActiveGameEffect gameEffect = gameEffects.get(type);
        if (gameEffect != null) {
            gameEffect.forceStop();
        }
    }

    public void addDot(DamageOverTime dot, int ticks, LivingGameEntity damager) {
        dotMap.compute(dot, (d, list) -> {
            (list = list != null ? list : new DotInstanceList(dot, this.entity)).add(new DotInstance(entity, damager, ticks));

            return list;
        });
    }

    /**
     * Returns true if this entity has the given effect.
     *
     * @param type - Type.
     * @return true if this entity has the given effect.
     */
    public boolean hasEffect(Effects type) {
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
    public void notifyChatIncoming(GamePlayer player) {
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

        player.sendMessage(prefix + message);
    }

    /**
     * Notifiers player about outgoing damage for this data.
     *
     * @param player - Player to notify.
     */
    public void notifyChatOutgoing(GamePlayer player) {
        final double damage = this.lastDamage;

        if (damage < 1) {
            return;
        }

        final String prefix = "&7[&a⚔&7] &f";

        player.sendMessage(prefix + "&l%.2f &fusing &l%s &fto &l%s%s".formatted(
                damage,
                Chat.capitalize(this.getLastDamageCauseNonNull()),
                this.entity.getName(),
                this.isCrit ? " &b&lCRITICAL" : ""
        ));
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

    /**
     * Gets the entity data for the given entity from the current game instance.
     *
     * @param entity - Entity.
     * @return the entity data for the given entity from the current game instance.
     */
    @Nonnull
    @Deprecated(forRemoval = true)
    public static EntityData of(@Nonnull LivingEntity entity) {
        final LivingGameEntity gameEntity = CF.getEntity(entity);

        if (gameEntity == null) {
            throw new IllegalStateException("cannot find game entity for " + entity);
        }

        return gameEntity.getData();
    }

    public static void die(@Nonnull LivingEntity livingEntity) {
        final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
        if (gameEntity == null) {
            return;
        }

        gameEntity.dieBy(EnumDamageCause.SUICIDE);
    }

    /**
     * Reset damage data for every entity in the current game instance.
     */
    public static void resetDamageData() {
        final IGameInstance instance = Manager.current().getCurrentGame();

        if (instance instanceof GameInstance) {
            CF.getEntities(LivingGameEntity.class).forEach(entity -> entity.getData().resetDamage());
        }
    }
}
