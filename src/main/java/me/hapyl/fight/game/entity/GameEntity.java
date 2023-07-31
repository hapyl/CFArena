package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GameEntity {

    protected final UUID uuid;
    protected final EntityData entityData;
    @Nonnull protected LivingEntity entity;
    @Nonnull
    protected EntityAttributes attributes;
    protected boolean wasHit; // Used to check if player was hit by custom damage
    protected double health;
    @Nonnull protected EntityState state;
    private boolean canMove;
    private Shield shield;

    // TODO (hapyl): 031, Jul 31: Realistically this should be or there should a more base and basic class, without data and attributes etc

    public GameEntity(@Nonnull LivingEntity entity) {
        this.entity = entity;
        this.uuid = entity.getUniqueId();
        this.entityData = new EntityData(this);
        this.attributes = new EntityAttributes(this, new Attributes());
        this.wasHit = false;
        this.health = attributes.get(AttributeType.HEALTH);
        this.state = EntityState.ALIVE;
        this.canMove = true;
    }

    @Nonnull
    public LivingEntity getEntity() {
        return entity;
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

    public void damage(double damage, @Nullable LivingEntity damager) {
        damage(damage, CF.getEntity(damager));
    }

    public void damage(double damage) {
        damage(damage, (GameEntity) null, null);
    }

    @Super
    public void damage(double damage, @Nullable GameEntity damager, @Nullable EnumDamageCause cause) {
        // Don't reassign the damage if self damage!
        // That's the whole point of the system to
        // award the last damager even if player killed themselves.
        if (damager != null && entity != damager) {
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
        damage(d, (GameEntity) null, cause);
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

    public void onDeath() {
        remove();
    }

    @Event
    public void onStop(GameInstance instance) {
        remove();
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

    public void remove() {
        entity.remove();
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

    public void setLastDamager(GameEntity lastDamager) {
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
        return "GameEntity{" + entity.getUniqueId() + "}";
    }

    public void teleport(Location location) {
        entity.teleport(location);
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

    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    public EntityData getData() {
        return entityData;
    }

    public String getStatusString() {
        return state.string;
    }

    public <T extends LivingEntity> boolean is(@Nonnull Class<T> clazz) {
        return clazz.isInstance(entity);
    }

    public boolean is(@Nullable LivingEntity entity) {
        return this.entity == entity;
    }

    public boolean isNot(LivingEntity player) {
        return !is(player);
    }

    public <T extends LivingEntity> boolean isNot(@Nonnull Class<T> clazz) {
        return !is(clazz);
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return entity.hasPotionEffect(type);
    }

    public PotionEffect getPotionEffect(PotionEffectType type) {
        return entity.getPotionEffect(type);
    }

    public String getName() {
        return entity.getName();
    }

    @Nonnull
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    public void asPlayer(Consumer<Player> consumer) {
        if (!(entity instanceof Player player)) {
            return;
        }

        consumer.accept(player);
    }

    public final UUID getUUID() {
        return uuid;
    }

    @Nonnull
    public World getWorld() {
        final World world = getLocation().getWorld();

        if (world != null) {
            return world;
        }

        throw new IllegalArgumentException("unloaded world!!!");
    }

    public boolean isValid() {
        return isValid(null);
    }

    public boolean isValid(Player player) {
        // null entities, self or armor stands are not valid
        if (is(player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or invisible entities are not valid
        if (entity.isDead() || entity.isInvisible()) {
            return false;
        }

        // players are only valid if they are alive and not on the same team
        if (entity instanceof Player targetPlayer) {
            // creative players should not be valid!
            if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
                return false;
            }

            if (Manager.current().isGameInProgress() && !CF.getOrCreatePlayer(targetPlayer).isAlive()) {
                return false;
            }
            return !GameTeam.isTeammate(player, targetPlayer);
        }

        // Dummy check
        if (entity.getScoreboardTags().contains("dummy")) {
            return true;
        }

        return entity.hasAI();
    }

    public boolean hasLineOfSight(@Nonnull LivingEntity entity) {
        return this.entity.hasLineOfSight(entity);
    }

    public void addPotionEffect(PotionEffect effect) {
        entity.addPotionEffect(effect);
    }

    public void setVelocity(Vector vector) {
        entity.setVelocity(vector);
    }

    public boolean hasTag(String s) {
        return entity.getScoreboardTags().contains(s);
    }

    public void removeTag(String s) {
        entity.getScoreboardTags().remove(s);
    }

    public double getKnockback() {
        return getAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
    }

    public void setKnockback(double d) {
        setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, d);
    }

    public void modifyKnockback(double d, Consumer<GameEntity> consumer) {
        final double knockback = getKnockback();

        setKnockback(d);
        consumer.accept(this);
        setKnockback(knockback);
    }

    public void setInvulnerable(boolean b) {
        entity.setInvulnerable(b);
    }

    public double getEyeHeight() {
        return entity.getEyeHeight();
    }
    // this spawns globally

    public void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        PlayerLib.spawnParticle(location, particle, amount, x, y, z, speed);
    }

    public void setFreezeTicks(int tick) {
        entity.setFreezeTicks(tick);
    }

    public void damageTick(double damage, EnumDamageCause cause, int tick) {
        damageTick(damage, (GameEntity) null, cause, tick);
    }

    public int getNoDamageTicks() {
        return entity.getNoDamageTicks();
    }

    public void setFireTicks(int tick) {
        entity.setFireTicks(tick);
    }
}
