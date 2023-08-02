package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class LivingGameEntity extends GameEntity {

    protected final EntityData entityData;
    @Nonnull
    protected EntityAttributes attributes;
    protected boolean wasHit; // Used to check if player was hit by custom damage
    protected double health;
    @Nonnull protected EntityState state;
    private boolean canMove;
    private Shield shield;

    public LivingGameEntity(@Nonnull LivingEntity entity) {
        this(entity, new Attributes());
    }

    public LivingGameEntity(@Nonnull LivingEntity entity, @Nonnull Attributes attributes) {
        super(entity);
        this.entityData = new EntityData(this);
        this.attributes = new EntityAttributes(this, attributes);
        this.wasHit = false;
        this.health = attributes.get(AttributeType.HEALTH);
        this.state = EntityState.ALIVE;
        this.canMove = true;

        entity.setHealth(0.1d);
    }

    public void sendWarning(String warning, int stay) {
        asPlayer(player -> Chat.sendTitle(player, "&4&l⚠", warning, 0, stay, 5));
    }

    public void sendMessage(String message, Object... objects) {
        Chat.sendMessage(entity, message, objects);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        asPlayer(player -> Chat.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut));
    }

    public void sendSubtitle(String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle("", subtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionbar(String text, Object... objects) {
        asPlayer(player -> Chat.sendActionbar(player, text, objects));
    }

    public void playSound(Sound sound, final float pitch) {
        asPlayer(player -> PlayerLib.playSound(player, sound, Numbers.clamp(pitch, 0.0f, 2.0f)));
    }

    public void playSound(Location location, Sound sound, float pitch) {
        PlayerLib.playSound(location, sound, pitch);
    }

    /**
     * Returns true if entity has died during the game and currently spectating or waiting for respawn.
     *
     * @return true if an entity has died during the game and currently spectating or waiting for respawn.
     */
    public boolean isDead() {
        return state == EntityState.DEAD;
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
    public boolean canMove() {
        return canMove;
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
        onDeath();
    }

    @Event
    @Nullable
    public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
        return null;
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
        return attributes.get(AttributeType.HEALTH);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
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

    public double getWalkSpeed() {
        return getAttributeValue(Attribute.GENERIC_MOVEMENT_SPEED);
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

    public void setTarget(@Nullable LivingEntity entity) {
        if (this.entity instanceof Creature creature) {
            creature.setTarget(entity);
        }
    }

    public void setTarget(@Nullable LivingGameEntity entity) {
        setTarget(entity == null ? null : entity.getEntity());
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

    public boolean isType(@Nonnull GameEntities type) {
        if (this instanceof NamedGameEntity named) {
            return named.type.equals(type.type);
        }

        return false;
    }
}
