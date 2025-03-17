package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.Important;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Type;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Used to store named damage, effects, etc.
 */
public final class EntityData implements Ticking {

    private static final long ASSIST_DURATION = TimeUnit.SECONDS.toMillis(10);

    private final Map<EffectType, ActiveGameEffect> gameEffects;
    private final Map<GamePlayer, Double> damageTaken;
    private final Map<DamageCause, Integer> attackCooldown;

    private final Cache<GamePlayer> assistingPlayers;

    private final LivingGameEntity entity;

    @Important(value = "Notifies the event that the damage is named, not vanilla.")
    boolean wasHit;

    @Nullable private GameEntity lastDamager;
    @Nullable private DamageCause lastDamageCause;

    public EntityData(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.damageTaken = Maps.newHashMap();
        this.gameEffects = Maps.newConcurrentMap();
        this.attackCooldown = Maps.newConcurrentMap();
        this.assistingPlayers = Cache.ofSet(ASSIST_DURATION);
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
     * Gets the actual map of the game effects.
     *
     * @return game effect map.
     */
    @Nonnull
    public Map<EffectType, ActiveGameEffect> getGameEffects() {
        return gameEffects;
    }

    /**
     * Gets the actual map of damage taken.
     *
     * @return damage taken map.
     */
    @Nonnull
    public Map<GamePlayer, Double> getDamageTaken() {
        return damageTaken;
    }

    /**
     * Gets the cause of the last taken damage.
     *
     * @return the last cause of the taken damage.
     */
    @Nullable
    public DamageCause getLastDamageCause() {
        return lastDamageCause;
    }

    /**
     * Sets the cause of the last-taken damage.
     *
     * @param cause - Cause.
     */
    public void setLastDamageCause(@Nullable DamageCause cause) {
        this.lastDamageCause = cause;
    }

    /**
     * Gets the cause of the last taken damage, defaults to {@link DamageCause#ENTITY_ATTACK}.
     *
     * @return the cause of the last taken damage, defaults to {@link DamageCause#ENTITY_ATTACK}.
     */
    @Nonnull
    public DamageCause getLastDamageCauseNonNull() {
        return lastDamageCause == null ? DamageCause.ENTITY_ATTACK : lastDamageCause;
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
    public GameEntity lastDamager() {
        // Validate if the damage is still alive, unless it's a player
        if (lastDamager != null) {
            if (!(lastDamager instanceof GamePlayer) && lastDamager instanceof LivingGameEntity livingDamager && livingDamager.isDead()) {
                lastDamager = null;
            }
        }

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
        return lastDamager() instanceof LivingGameEntity livingDamager ? livingDamager : null;
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
     * Sets the last damage cause if the last damage was native.
     *
     * @param cause - Bukkit cause.
     */
    public void setLastDamageCauseIfNative(@Nonnull EntityDamageEvent.DamageCause cause) {
        if (isNativeDamage()) {
            lastDamageCause = DamageCause.byBukkitCause(cause);
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
     */
    public void addEffect(@Nonnull EffectType type, int amplifier, int duration) {
        // Check for effect resistance
        if (type.getEffect().getType() == Type.NEGATIVE && entity.hasEffectResistanceAndNotify()) {
            return;
        }

        final ActiveGameEffect effect = gameEffects.get(type);

        if (effect != null) {
            effect.triggerUpdate();
            effect.setRemainingTicks(duration);
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
    public void clearEffect(EffectType type) {
        gameEffects.remove(type);
    }

    /**
     * Removes the effect from this entity.
     *
     * @param type - Type.
     */
    public void removeEffect(EffectType type) {
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
    public boolean hasEffect(EffectType type) {
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
     * </ul>
     */
    public void resetDamage() {
        lastDamager = null;
        lastDamageCause = null;
    }

    public void addAssistingPlayer(@Nonnull GamePlayer player) {
        this.assistingPlayers.add(player);
    }

    /**
     * Gets a copy of players who has assisted in the last {@link #ASSIST_DURATION}.
     *
     * @return a copy of players who has assisted in the last {@link #ASSIST_DURATION}.
     */
    @Nonnull
    public Set<GamePlayer> getAssistingPlayers() {
        return new HashSet<>(assistingPlayers);
    }

    @Override
    public void tick() {
        // Tick effects
        gameEffects.values().forEach(ActiveGameEffect::tick);

        // Tick attack cooldowns
        tickAttackCooldowns();
    }

    public boolean hasAttackCooldown(@Nonnull DamageCause cause) {
        final int cooldown = attackCooldown.getOrDefault(cause, 0);

        return !cause.isEnvironmentDamage() && cooldown > 0;
    }

    public void startAttackCooldown(@Nonnull DamageCause cause, int cooldown) {
        attackCooldown.put(cause, cooldown);
    }

    private void tickAttackCooldowns() {
        final Iterator<Map.Entry<DamageCause, Integer>> iterator = attackCooldown.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<DamageCause, Integer> next = iterator.next();
            final int value = next.getValue() - 1;

            if (value > 0) {
                next.setValue(value);
            }
            else {
                iterator.remove();
            }
        }
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

        return gameEntity.getEntityData();
    }

    public static void die(@Nonnull LivingEntity livingEntity) {
        final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
        if (gameEntity == null) {
            return;
        }

        gameEntity.dieBy(DamageCause.SUICIDE);
    }

    /**
     * Reset damage data for every entity in the current game instance.
     */
    public static void resetDamageData() {
        final IGameInstance instance = Manager.current().getCurrentGame();

        if (instance instanceof GameInstance) {
            CF.getEntities(LivingGameEntity.class).forEach(entity -> entity.getEntityData().resetDamage());
        }
    }
}
