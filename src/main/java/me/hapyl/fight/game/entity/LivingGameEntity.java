package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.CooldownData;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.entity.packet.EntityPacketFactory;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.fight.game.ui.display.DamageDisplay;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import net.minecraft.network.protocol.Packet;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LivingGameEntity extends GameEntity {

    private static final Draw FEROCITY_PARTICLE_DATA = new FerocityFx();
    private static final int FEROCITY_HIT_CD = 9;

    protected final EntityData entityData;

    private final Set<EnumDamageCause> immunityCauses = Sets.newHashSet();
    private final EntityMetadata metadata;
    private final EntityCooldown cooldown;
    private final EntityPacketFactory packetFactory;
    private final Random random;
    @Nonnull
    protected EntityAttributes attributes;
    protected boolean wasHit; // Used to check if an entity was hit by custom damage
    protected double health;
    @Nonnull protected EntityState state;

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
        this.packetFactory = new EntityPacketFactory(this);
        this.random = new Random();
        super.base = false;

        // The actual health of the entity is set to 0.1 to remove the weird
        // hearts when it dies, since the health is not actually decreased.
        // Could really make it so entity actually has the health,
        // but I'm pretty sure the max health is 2048, which is wack
        entity.setHealth(0.1d);

        defaultVanillaAttributes();
        updateAttributes();
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
        return metadata.canMove.getValue();
    }

    @Deprecated
    public void setCanMove(boolean canMove) {
        metadata.canMove.setValue(canMove);
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
        final int noDamageTicks = entity.getNoDamageTicks();

        tick = Numbers.clamp(tick, 0, maximumNoDamageTicks);

        if (getInternalNoDamageTicks() > 0) { // noDamageTicks > 0 ||
            return;
        }

        setInternalNoDamageTicks(tick);

        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(0);

        damage(damage, damager, cause == null ? EnumDamageCause.ENTITY_ATTACK : cause);

        entity.setMaximumNoDamageTicks(maximumNoDamageTicks);
    }

    public long getInternalNoDamageTicks() {
        final CooldownData data = cooldown.getData(Cooldown.NO_DAMAGE);
        return data != null ? data.getTimeLeft() : 0;
    }

    private void setInternalNoDamageTicks(int ticks) {
        cooldown.startCooldown(Cooldown.NO_DAMAGE, ticks * 50L);
    }

    /**
     * Gets the distance to the ground from the feet with ~0.05 error.
     *
     * @return the distance to the ground.
     */
    public double getDistanceToGround() {
        final Location location = getLocation();
        final World world = getWorld();
        final double y = location.getY();

        while (true) {
            if (location.getY() <= world.getMinHeight()) {
                break;
            }

            if (!location.getBlock().isEmpty()) {
                break;
            }

            location.subtract(0.0d, 0.05d, 0.0d);
        }

        return y - location.getY();
    }

    @Nullable
    public GameTeam getTeam() {
        return GameTeam.getEntryTeam(Entry.of(this));
    }

    public void setGlowing(@Nonnull Player player, @Nonnull ChatColor color, int duration) {
        Glowing.glow(entity, color, duration, player);
    }

    public void setGlowing(@Nonnull Player player, @Nonnull ChatColor color) {
        Glowing.glowInfinitly(entity, color, player);
    }

    public void setGlowingColor(@Nonnull Player player, @Nonnull ChatColor color) {
        final Glowing glowing = EternaPlugin.getPlugin().getRegistry().glowingManager.getGlowing(player, entity);

        if (glowing != null) {
            glowing.setColor(color);
        }
        else {
            setGlowing(player, color);
        }
    }

    public void stopGlowing(@Nonnull Player player) {
        Glowing.stopGlowing(player, entity);
    }

    @Nonnull
    public Location getLocationAnchored() {
        final Location location = getLocation();
        return GamePlayer.anchorLocation(location);
    }

    public void damageTick(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, int tick) {
        damageTick(damage, CF.getEntity(damager), cause, tick);
    }

    public void heal(double amount) {
        final EntityAttributes attributes = getAttributes();
        amount = attributes.calculateHealing(amount);

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
    @OverridingMethodsMustInvokeSuper
    public void decreaseHealth(@Nonnull DamageInstance instance) {
        final double damage = instance.getDamage();
        final boolean willDie = health - damage <= 0.0d;

        // GameDeathEvent here to not decrease health below lethal
        if (willDie && new GameDeathEvent(instance).callAndCheck()) {
            return;
        }

        this.health -= damage;

        if (willDie) {
            die(true);
        }

        // Damage indicator
        displayDamage(instance);
    }

    public void displayDamage(@Nonnull DamageInstance instance) {
        final double damage = instance.getDamage();

        if (damage < 1.0d || entity instanceof ArmorStand || entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return;
        }

        final EnumDamageCause cause = instance.getCause();

        if (cause != null) {
            final String format = cause.getDamageCause().getDamageFormat().getFormat();
            new DamageDisplay(damage, instance.isCrit()).display(getLocation());
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
        entity.addPotionEffect(new PotionEffect(type, duration, amplifier, false, false));
    }

    public void dieBy(@Nonnull EnumDamageCause cause) {
        setLastDamageCause(cause);
        die(true);
    }

    @OverridingMethodsMustInvokeSuper
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

    @PreprocessingMethod
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

    @PreprocessingMethod
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

    @Nonnull
    public String getHealthFormatted() {
        return "&c&l%.0f".formatted(Math.ceil(health)) + " &c❤";
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

    public double getMaxHealth() {
        return attributes.get(AttributeType.MAX_HEALTH);
    }

    @Override
    public String toString() {
        return "LivingGameEntity{" + entity.getType() + "@" + entity.getUniqueId() + "}";
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

    public void setAttributeValue(@Nonnull Attribute attribute, double value) {
        final AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.setBaseValue(value);
    }

    public void modifyAttributeValue(@Nonnull Attribute attribute, double value) {
        setAttributeValue(attribute, getAttributeValue(attribute) + value);
    }

    public double getAttributeValue(@Nonnull Attribute attribute) {
        final AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return 0.0d;
        }

        return instance.getBaseValue();
    }

    @Nonnull
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

    @Deprecated/*I hate this*/
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

    /**
     * Spawns a particle at this entity location for everyone to see.
     *
     * @param particle - Particle.
     * @param amount   - Amount.
     */
    public void spawnWorldParticle(Particle particle, int amount) {
        spawnWorldParticle(particle, amount, 0, 0, 0, 0);
    }

    /**
     * Spawns a particle at this entity location for everyone to see.
     *
     * @param particle - Particle.
     * @param amount   - Amount.
     * @param x        - X offset.
     * @param y        - Y offset.
     * @param z        - Z offset.
     * @param speed    - Speed.
     */
    public void spawnWorldParticle(Particle particle, int amount, double x, double y, double z, float speed) {
        spawnWorldParticle(getLocation(), particle, amount, x, y, z, speed);
    }

    /**
     * Spawns a particle at the given location for everyone to see.
     *
     * @param location - Location.
     * @param particle - Particle.
     * @param amount   - Amount.
     * @param x        - X offset.
     * @param y        - Y offset.
     * @param z        - Z offset.
     * @param speed    - Speed.
     */
    public void spawnWorldParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        PlayerLib.spawnParticle(location, particle, amount, x, y, z, speed);
    }

    public <T> void spawnWorldParticle(Location location, Particle particle, int amount, double x, double y, double z, T data) {
        final World world = location.getWorld();
        if (world == null || particle.getDataType() != data.getClass()) {
            return;
        }

        world.spawnParticle(particle, location, amount, x, y, z, data);
    }

    public void spawnWorldParticle(Location location, Particle particle, int amount) {
        PlayerLib.spawnParticle(location, particle, amount);
    }

    /**
     * Spawns the particle for this entity at the given location.
     *
     * @param location - Location.
     * @param particle - Particle.
     * @param amount   - Amount.
     * @param x        - X offset.
     * @param y        - Y offset.
     * @param z        - Z offset.
     * @param speed    - Speed.
     */
    public void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed) {
        asPlayer(player -> {
            PlayerLib.spawnParticle(player, location, particle, amount, x, y, z, speed);
        });
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

    public void executeFerocity(double damage, LivingGameEntity lastDamager, int ferocityStrikes) {
        if (hasCooldown(Cooldown.FEROCITY)) {
            return;
        }

        PlayerLib.playSound(getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);

        for (int i = 0; i < ferocityStrikes; i++) {
            GameTask.runLater(() -> damageFerocity(damage, lastDamager), i * 3 + FEROCITY_HIT_CD);
        }

        startCooldown(Cooldown.FEROCITY);
    }

    public void damageFerocity(double damage, LivingGameEntity lastDamager) {
        if (isDead()) {
            return;
        }

        // Ferocity knock-back is kinda crazy, using this little hack to remove it.
        entityData.setLastDamager(lastDamager);
        damageTick(damage, (LivingGameEntity) null, EnumDamageCause.FEROCIY, 1);

        // Fx
        final Location location = getLocation();

        PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
        PlayerLib.playSound(location, Sound.ENTITY_DONKEY_HURT, 1.25f);

        final double eyeHeight = entity.getEyeHeight();

        final double x = randomDouble(0.5d, 1.25d);
        final double z = randomDouble(0.5d, 1.25d);

        Geometry.drawLine(location.clone().add(x, eyeHeight, z), location.clone().subtract(x, 0, z), 0.2d, FEROCITY_PARTICLE_DATA);
    }

    public void addPotionEffect(EffectType effectType, int duration, int amplifier) {
        addPotionEffect(effectType.getType().createEffect(duration, amplifier));
    }

    public int getMaximumNoDamageTicks() {
        return getEntity().getMaximumNoDamageTicks();
    }

    public void setMaximumNoDamageTicks(int i) {
        getEntity().setMaximumNoDamageTicks(i);
    }

    public void setVisualFire(boolean b) {
        entity.setVisualFire(b);
    }

    public void playSoundAndCut(@Nonnull Sound sound, float pitch, int cut) {
        asPlayer(player -> PlayerLib.playSoundAndCut(player, sound, pitch, cut));
    }

    public void playHurtSound() {
        final Sound hurtSound = getEntity().getHurtSound();

        if (hurtSound == null) {
            return;
        }

        playWorldSound(hurtSound, 1.0f);
    }

    public int getMaxFreezeTicks() {
        return entity.getMaxFreezeTicks();
    }

    @Nonnull
    public Location getMidpointLocation() {
        return getLocation().add(0, getEyeHeight() / 2, 0);
    }

    public void playDodgeFx() {
        final Location location = getLocation();

        final short x = randomShort();
        final short z = randomShort();
        final float yaw = location.getYaw();
        final float pitch = location.getPitch();

        final Packet<?> packet = packetFactory.createRelMovePacket(x, (short) 0, z, yaw, pitch);

        packetFactory.sendPacket(packet);
        packetFactory.sendPacketDelayed(packetFactory.createTeleportPacket(), 2);

        final Location fxLocation = getMidpointLocation();

        // Fx
        playWorldSound(fxLocation, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.75f);
        playWorldSound(fxLocation, Sound.ENTITY_WARDEN_LISTENING, 1.75f);

        spawnWorldParticle(fxLocation, Particle.CRIT, 10, 0.25d, 0.5d, 0.25d, 0.25f);

        new BuffDisplay("DODGED", 5).display(fxLocation);
    }

    @Nonnull
    public String getScoreboardName() {
        return uuid.toString();
    }

    protected void defaultVanillaAttributes() {
        setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d);
        setAttributeValue(Attribute.GENERIC_ATTACK_SPEED, 2.0d);
        setAttributeValue(Attribute.GENERIC_ATTACK_DAMAGE, 1.0d);
        setAttributeValue(Attribute.GENERIC_ARMOR, -100.0d); // Remove armor bars
    }

    private void updateAttributes() {
        attributes.forEach((type, d) -> {
            type.attribute.update(this, d);
        });
    }

    private double randomDouble(double origin, double bound) {
        final double value = random.nextDouble(origin, bound);
        return random.nextBoolean() ? value : -value;
    }

    private short randomShort() {
        return (short) (randomDouble(0.0d, 1.0d) * 8192);
    }
}
