package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreconditionMethod;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LivingGameEntity extends GameEntity {

    protected final EntityData entityData;
    private final Set<EnumDamageCause> immunityCauses = Sets.newHashSet();
    private final EntityMetadata metadata;
    @Nonnull
    protected EntityAttributes attributes;
    protected boolean wasHit; // Used to check if player was hit by custom damage
    protected double health;
    @Nonnull protected EntityState state;
    private Shield shield;
    private final EntityCooldown cooldown;

    public LivingGameEntity(@Nonnull LivingEntity entity) {
        this(entity, new Attributes(entity));
    }

    public LivingGameEntity(@Nonnull LivingEntity entity, @Nonnull Attributes attributes) {
        super(entity);
        this.entityData = new EntityData(this);
        this.attributes = new EntityAttributes(this, attributes);
        this.wasHit = false;
        this.health = attributes.get(AttributeType.MAX_HEALTH);
        this.state = EntityState.ALIVE;
        this.metadata = new EntityMetadata();
        this.cooldown = new EntityCooldown(this);
        super.base = false;

        entity.setHealth(0.1d);
    }

    /**
     * Returns true if entity has died during the game and currently spectating or waiting for respawn.
     *
     * @return true if an entity has died during the game and currently spectating or waiting for respawn.
     */
    public boolean isDead() {
        return state == EntityState.DEAD;
    }

    public boolean isDeadOrRespawning() {
        return state == EntityState.DEAD || state == EntityState.RESPAWNING;
    }

    @Nonnull
    public EntityState getState() {
        return state;
    }

    public void setState(@Nonnull EntityState state) {
        this.state = state;
    }

    /**
     * Returns true if entity can move; false otherwise.
     *
     * @return true if an entity can move; false otherwise.
     */
    @Deprecated
    public boolean canMove() {
        return metadata.CAN_MOVE.getValue();
    }

    @Deprecated
    public void setCanMove(boolean canMove) {
        metadata.CAN_MOVE.setValue(canMove);
    }

    @Nonnull
    public EntityMetadata getMetadata() {
        return metadata;
    }

    @Nonnull
    public EntityAttributes getAttributes() {
        return attributes;
    }

    public void removeEffect(GameEffectType type) {
        entityData.removeEffect(type);
    }

    @Override
    public void remove() {
        entity.setHealth(0);
    }

    public void damage(double damage, @Nullable LivingEntity damager) {
        damage(damage, CF.getEntity(damager));
    }

    public void damage(double damage) {
        damage(damage, (LivingGameEntity) null, null);
    }

    @Super
    public void damage(double damage, @Nullable GameEntity damager, @Nullable EnumDamageCause cause) {
        // Don't reassign the damage if self damage!
        // That's the whole point of the system to
        // award the last damager even if player killed themselves.
        if (damager != null && !this.equals(damager)) {
            entityData.setLastDamager(damager);
        }

        if (cause != null) {
            entityData.setLastDamageCause(cause);
        }

        entityData.setLastDamage(damage);

        // Call the damage event
        entityData.wasHit = true; // This tag is VERY important for calculations
        entity.damage(damage, damager == null ? null : damager.getEntity());
        entityData.wasHit = false;
    }

    public void damage(double d, EnumDamageCause cause) {
        damage(d, (LivingGameEntity) null, cause);
    }

    public void damage(double d, GameEntity damager) {
        damage(d, damager, null);
    }

    public void damage(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        damage(damage, CF.getEntity(damager), cause);
    }

    /**
     * Performs damage, with a given no damage ticks.
     *
     * @param damage  - Damage.
     * @param damager - Damager.
     * @param cause   - Cause.
     * @param tick    - No damage ticks.
     */
    public void damageTick(double damage, @Nullable GameEntity damager, @Nullable EnumDamageCause cause, int tick) {
        final int maximumNoDamageTicks = entity.getMaximumNoDamageTicks();
        tick = Numbers.clamp(tick, 0, maximumNoDamageTicks);

        entity.setMaximumNoDamageTicks(tick);
        damage(damage, damager, cause == null ? EnumDamageCause.ENTITY_ATTACK : cause);
        entity.setMaximumNoDamageTicks(maximumNoDamageTicks);
    }

    public void damageTick(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, int tick) {
        damageTick(damage, CF.getEntity(damager), cause, tick);
    }

    public void heal(double amount) {
        this.health = Numbers.clamp(health + amount, 0.5d, getMaxHealth());

        // Fx
        PlayerLib.spawnParticle(
                entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                Particle.HEART,
                (int) Numbers.clamp(amount / 5, 1, 10),
                0.44, 0.2, 0.44, 0.015f
        );
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    public void decreaseHealth(double damage) {
        this.health -= damage;
        if (this.health <= 0.0d) {
            this.die(true);
        }
    }

    public boolean isAlive() {
        return state == EntityState.ALIVE;
    }

    public void removePotionEffect(PotionEffectType type) {
        entity.removePotionEffect(type);
    }

    public Map<GameEffectType, ActiveGameEffect> getActiveEffects() {
        return entityData.getGameEffects();
    }

    public boolean hasEffect(GameEffectType type) {
        return entityData.hasEffect(type);
    }

    public void addEffect(GameEffectType type, int ticks) {
        addEffect(type, ticks, false);
    }

    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
        entity.addPotionEffect(type.createEffect(duration, amplifier));
    }

    public void die(boolean force) {
        if (this.health > 0.0d && !force) {
            return;
        }

        // Don't kill creative players
        if (!shouldDie()) {
            return;
        }

        state = EntityState.DEAD;
        cooldown.stopCooldowns();
        onDeath();
    }

    public boolean isImmune(@Nonnull EnumDamageCause cause) {
        return immunityCauses.contains(cause);
    }

    public void addImmune(@Nonnull EnumDamageCause cause, @Nullable EnumDamageCause... other) {
        immunityCauses.add(cause);
        if (other != null) {
            Collections.addAll(immunityCauses, other);
        }
    }

    public void removeImmune(@Nullable EnumDamageCause... causes) {
        if (causes != null) {
            for (EnumDamageCause cause : causes) {
                immunityCauses.remove(cause);
            }
        }
    }

    @PreconditionMethod
    @Nullable
    public final DamageOutput onDamageTaken0(@Nonnull DamageInput input) {
        final EnumDamageCause cause = input.getDamageCause();

        if (cause != null && immunityCauses.contains(cause)) {
            final GameEntity damager = input.getDamager();
            if (damager != null) {
                damager.sendMessage(ChatColor.RED + getNameUnformatted() + " is immune to this kind of damage!");
            }

            return DamageOutput.CANCEL;
        }

        return onDamageTaken(input);
    }

    @Event
    @Nullable
    public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
        return null;
    }

    @PreconditionMethod
    @Nullable
    public final DamageOutput onDamageDealt0(DamageInput input) {
        return onDamageDealt(input);
    }

    @Event
    @Nullable
    public DamageOutput onDamageDealt(@Nonnull DamageInput input) {
        return null;
    }

    public boolean shouldDie() {
        return true;
    }

    @Nonnull
    public EnumDamageCause getLastDamageCause() {
        return entityData.getLastDamageCauseNonNull();
    }

    public void setLastDamageCause(EnumDamageCause lastDamageCause) {
        entityData.setLastDamageCause(lastDamageCause);
    }

    public String getHealthFormatted() {
        return String.valueOf(Math.ceil(health));
    }

    @Nullable
    public GameEntity getLastDamager() {
        return entityData.getLastDamager();
    }

    public void setLastDamager(LivingGameEntity lastDamager) {
        if (lastDamager != null) {
            entityData.setLastDamager(lastDamager);
        }
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Nullable
    public Shield getShield() {
        return shield;
    }

    public void setShield(@Nonnull Shield shield) {
        this.shield = shield;
    }

    public double getMaxHealth() {
        return attributes.get(AttributeType.MAX_HEALTH);
    }

    @Override
    public String toString() {
        return "LivingGameEntity{" + entity.getUniqueId() + "}";
    }

    public void addEffect(GameEffectType type, int ticks, boolean override) {
        entityData.addEffect(type, ticks, override);
    }

    public double getMinHealth() {
        return 0.5d;
    }

    public float getWalkSpeed() {
        return (float) getAttributeValue(Attribute.GENERIC_MOVEMENT_SPEED);
    }

    public void setWalkSpeed(double value) {
        setAttributeValue(Attribute.GENERIC_MOVEMENT_SPEED, value);
    }

    public void setAttributeValue(Attribute attribute, double value) {
        final AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.setBaseValue(value);
    }

    public double getAttributeValue(Attribute attribute) {
        final AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return 0.0d;
        }

        return instance.getBaseValue();
    }

    public EntityData getData() {
        return entityData;
    }

    public String getStatusString() {
        return state.string;
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return entity.hasPotionEffect(type);
    }

    public PotionEffect getPotionEffect(PotionEffectType type) {
        return entity.getPotionEffect(type);
    }

    public void addPotionEffect(PotionEffect effect) {
        entity.addPotionEffect(effect);
    }

    public double getKnockback() {
        return getAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
    }

    public void setKnockback(double d) {
        setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, d);
    }

    public void modifyKnockback(double d, Consumer<LivingGameEntity> consumer) {
        final double knockback = getKnockback();

        setKnockback(d);
        consumer.accept(this);
        setKnockback(knockback);
    }

    public void setTargetClosest() {
        final LivingGameEntity living = Collect.nearestEntityPrioritizePlayers(getLocation(), 10, t -> true);

        if (living != null) {
            setTarget(living);
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.entity instanceof Creature creature) {
            return creature.getTarget();
        }

        return null;
    }

    public void setTarget(@Nullable LivingEntity entity) {
        if (this.entity instanceof Creature creature) {
            creature.setTarget(entity);
        }
    }

    public void setTarget(@Nullable LivingGameEntity entity) {
        setTarget(entity == null ? null : entity.getEntity());
    }
    // this spawns globally

    public void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        PlayerLib.spawnParticle(location, particle, amount, x, y, z, speed);
    }

    public void spawnParticle(Location location, Particle particle, int amount) {
        spawnParticle(location, particle, amount, 0, 0, 0, 0);
    }

    public void damageTick(double damage, EnumDamageCause cause, int tick) {
        damageTick(damage, (LivingGameEntity) null, cause, tick);
    }

    @Nonnull
    @Override
    public LivingGameEntity getGameEntity() {
        return this;
    }

    public boolean isType(@Nonnull GameEntities type) {
        if (this instanceof NamedGameEntity<?> named) {
            return named.type.equals(type.type);
        }

        return false;
    }

    public void spawnBuffDisplay(@Nonnull String string, int duration) {
        spawnDisplay(string, duration, BuffDisplay::new);
    }

    public void spawnDebuffDisplay(@Nonnull String string, int duration) {
        spawnDisplay(string, duration, DebuffDisplay::new);
    }

    public <T extends StringDisplay> void spawnDisplay(@Nonnull String string, int duration, @Nonnull BiFunction<String, Integer, T> fn) {
        fn.apply(string, duration).display(getLocation());
    }

    @Nonnull
    public EntityCooldown getCooldown() {
        return cooldown;
    }

    public boolean hasCooldown(@Nonnull Cooldown cooldown) {
        return this.cooldown.hasCooldown(cooldown);
    }

    public void startCooldown(@Nonnull Cooldown cooldown, long duration) {
        this.cooldown.startCooldown(cooldown, duration);
    }

    public void startCooldown(@Nonnull Cooldown cooldown) {
        this.cooldown.startCooldown(cooldown);
    }
}
