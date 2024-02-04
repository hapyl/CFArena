package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.dot.DamageOverTime;
import me.hapyl.fight.game.dot.DotInstanceList;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.CooldownData;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.entity.packet.EntityPacketFactory;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.display.*;
import me.hapyl.fight.util.*;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.ai.AI;
import me.hapyl.spigotutils.module.ai.MobAI;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LivingGameEntity extends GameEntity implements Ticking {

    private static final Draw FEROCITY_PARTICLE_DATA = new FerocityFx();
    private static final int FEROCITY_HIT_CD = 9;
    private static final double ACTUAL_ENTITY_HEALTH = 0.1d;
    private static final String CC_SMALL_CAPS_NAME = SmallCaps.format(AttributeType.EFFECT_RESISTANCE.getName());

    public final EntityRandom random;
    protected final EntityData entityData;
    private final Set<EnumDamageCause> immunityCauses = Sets.newHashSet();
    private final EntityMetadata metadata;
    private final EntityCooldown cooldown;
    private final EntityPacketFactory packetFactory;
    private final EntityMemory memory;
    @Nonnull
    protected EntityAttributes attributes;
    protected boolean wasHit; // Used to check if an entity was hit by custom damage
    protected double health;
    @Nonnull protected EntityState state;
    protected int noCCTicks = 0;
    private AI ai;
    private int aliveTicks = 0;
    private boolean informImmune = true;
    private int inWaterTicks;

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
        this.metadata = new EntityMetadata(this);
        this.cooldown = new EntityCooldown(this);
        this.packetFactory = new EntityPacketFactory(this);
        this.memory = new EntityMemory(this);
        this.random = new EntityRandom();

        super.base = false;

        // The actual health of the entity is set to 0.1 to remove the weird
        // hearts when it dies, since the health is not actually decreased.
        // Could really make it so entity actually has the health,
        // but I'm pretty sure the max health is 2048, which is wack
        entity.setMaxHealth(ACTUAL_ENTITY_HEALTH);
        entity.setHealth(entity.getMaxHealth());

        applyAttributes();
    }

    /**
     * Gets the total number of ticks this entity has been alive for.
     * Other implementations of {@link LivingGameEntity} may modify how {@link #aliveTicks} are counted,
     * and it may not resemble the actual number of ticks this entity exists.
     *
     * @return the number of ticks this entity has been alive for.
     */
    public int aliveTicks() {
        return aliveTicks;
    }

    /**
     * Gets the {@link AI} of this entity.
     * The {@link AI} is a {@link me.hapyl.spigotutils.EternaAPI} module that allows to modify entity's AI easily.
     *
     * @return this entity's AI.
     * @throws IllegalStateException if entity is a {@link Player} or not supported.
     */
    @Nonnull
    public AI getMobAI() throws IllegalStateException {
        if (ai == null) {
            final Entity nmsEntity = Reflect.getMinecraftEntity(entity);

            if (!(nmsEntity instanceof EntityInsentient)) {
                throw new IllegalArgumentException("MobAI is not supported for " + getName());
            }

            ai = MobAI.of(entity);
        }

        return ai;
    }

    /**
     * Returns true if entity has died during the game and currently spectating or waiting for respawn.
     *
     * @return true if an entity has died during the game and currently spectating or waiting for respawn.
     */
    public boolean isDead() {
        return state == EntityState.DEAD || entity.isDead();
    }

    public boolean isDeadOrRespawning() {
        return state == EntityState.DEAD || state == EntityState.RESPAWNING;
    }

    @Nonnull
    public EntityMemory getMemory() {
        return memory;
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

    /**
     * Adds an {@link Effects} to this entity.
     *
     * @param effect   - Effect to add.
     * @param duration - Duration of the effect.
     *                 -1 for infinite duration.
     *                 <p>
     *                 <i>Keep in mind infinite effects are <b>not</b> removed at death!</i>
     * @param override - Should override?
     */
    public void addEffect(@Nonnull Effects effect, int duration, boolean override) {
        addEffect(effect, 0, duration, override);
    }

    /**
     * Adds an {@link Effects} to this entity.
     *
     * @param effect   - Effect to add.
     * @param duration - Duration of the effect.
     *                 -1 for infinite duration.
     *                 <p>
     *                 <i>Keep in mind infinite effects are <b>not</b> removed at death!</i>
     */
    public void addEffect(@Nonnull Effects effect, int duration) {
        addEffect(effect, duration, true);
    }

    /**
     * Adds an {@link Effects} to this entity.
     *
     * @param effect    - Effect to add.
     * @param amplifier - Amplifier.
     * @param duration  - Duration of the effect.
     *                  -1 for infinite duration.
     *                  <p>
     *                  <i>Keep in mind infinite effects are <b>not</b> removed at death!</i>
     */
    public void addEffect(@Nonnull Effects effect, int amplifier, int duration) {
        addEffect(effect, amplifier, duration, true);
    }

    /**
     * Adds an {@link Effects} to this entity.
     *
     * @param effect    - Effect to add.
     * @param amplifier - Amplifier (level) of the effect.
     * @param duration  - Duration of the effect.
     *                  -1 for infinite duration.
     *                  <p>
     *                  <i>Keep in mind infinite effects are <b>not</b> removed at death!</i>
     * @param override  - Should override?
     */
    public void addEffect(@Nonnull Effects effect, int amplifier, int duration, boolean override) {
        entityData.addEffect(effect, amplifier, duration, override);
    }

    /**
     * Returns true if this entity has the given {@link Effects}; false otherwise.
     *
     * @param effect - Effect to check.
     * @return true if this entity has the given {@link Effects}; false otherwise.
     */
    public boolean hasEffect(@Nonnull Effects effect) {
        return entityData.hasEffect(effect);
    }

    public boolean hasEffect(@Nonnull Effect effect) {
        final Effects enumEffect = Effects.byHandle(effect);

        return enumEffect != null && hasEffect(enumEffect);
    }

    public void removeEffect(@Nonnull Effects effect) {
        entityData.removeEffect(effect);
    }

    /**
     * Removes all the effect from this entity with the given type.
     *
     * @param type - Type.
     */
    public void removeEffectsByType(@Nonnull EffectType type) {
        for (Effects effect : Effects.values()) {
            if (effect.getEffect().getType() == type) {
                removeEffect(effect);
            }
        }
    }

    /**
     * Kills an entity with the death animation.
     */
    @Override
    public void kill() {
        entity.setHealth(0);
        state = EntityState.DEAD;
    }

    /**
     * Removes the entity from existence without death animation.
     */
    public void remove() {
        entity.remove();
        state = EntityState.DEAD;
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

    public void damageNoKnockback(double damage, @Nonnull LivingGameEntity damager) {
        damageNoKnockback(damage, damager, null);
    }

    public void damageNoKnockback(double damage, @Nonnull LivingGameEntity damager, @Nullable EnumDamageCause cause) {
        setLastDamager(damager);
        damage(damage, cause);
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

    @Nonnull
    public Location getEyeLocationOffset(double yOffset, double directionOffset) {
        final Location location = getEyeLocation();
        location.add(0, yOffset, 0);
        location.add(location.getDirection().normalize().multiply(directionOffset));

        return location;
    }

    public void playEffect(@Nonnull EntityEffect entityEffect) {
        entity.playEffect(entityEffect);
    }

    private void setInternalNoDamageTicks(int ticks) {
        cooldown.startCooldown(Cooldown.NO_DAMAGE, ticks * 50L);
    }

    public double getHealthToMaxHealthPercent() {
        return getHealth() / getMaxHealth();
    }

    public boolean isVisibleTo(@Nonnull Player player) {
        return player.canSee(entity);
    }

    public boolean isVisibleTo(@Nonnull GamePlayer player) {
        return isVisibleTo(player.getPlayer());
    }

    /**
     * Hides this entity for each online player.
     */
    public void hide() {
        Bukkit.getOnlinePlayers().forEach(this::hide);
    }

    /**
     * Hides this entity for the given player.
     *
     * @param player - Player.
     */
    public void hide(@Nonnull Player player) {
        player.hideEntity(Main.getPlugin(), entity);
    }

    /**
     * Shows this entity to each online player.
     */
    public void show() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    /**
     * Shows this entity to the given player.
     *
     * @param player - Player.
     */
    public void show(@Nonnull Player player) {
        player.showEntity(Main.getPlugin(), entity);
    }

    public void createExplosion(@Nonnull Location location, double explosionRadius, double explosionDamage, @Nullable EnumDamageCause cause) {
        Collect.nearbyEntities(location, explosionRadius).forEach(entity -> {
            double damage = explosionDamage;

            if (isTeammate(entity)) {
                damage /= 3.0d;
            }

            entity.damage(damage, this, cause);
        });

        // Fx
        Geometry.drawCircle(location, explosionRadius, Quality.NORMAL, new WorldParticle(Particle.CRIT));
        Geometry.drawCircle(location, explosionRadius + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANTMENT_TABLE));

        final int amountScaled = (int) (explosionRadius * 1.5d);
        final double offsetScaled = (explosionRadius - 2) * 0.8d;

        spawnWorldParticle(location, Particle.EXPLOSION_LARGE, amountScaled, offsetScaled, 0, offsetScaled, 0);
        playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1);
    }

    public boolean isSelfOrTeammate(@Nullable LivingGameEntity victim) {
        return equals(victim) || isTeammate(victim);
    }

    public boolean isSelfOrTeammateOrHasEffectResistance(@Nullable LivingGameEntity victim) {
        return isSelfOrTeammate(victim) || (victim != null && victim.hasEffectResistanceAndNotify(this));
    }

    public boolean isInWater() {
        return entity.isInWater();
    }

    @Nonnull
    public Location getLocationToTheLeft(double v) {
        return LocationHelper.getToTheLeft(getLocation(), v);
    }

    @Nonnull
    public Location getLocationToTheRight(double v) {
        return LocationHelper.getToTheRight(getLocation(), v);
    }

    public void setNoDamageTicks(int i) {
        entity.setNoDamageTicks(i);
    }

    public boolean hasEffectResistanceAndNotify(@Nullable GameEntity damager) {
        final boolean resist = hasEffectResistanceAndNotify();

        if (!resist) {
            entityData.setLastDamager(damager);
        }

        return resist;
    }

    public boolean hasEffectResistanceAndNotify() {
        if (noCCTicks > 0) {
            return true;
        }

        final boolean resist = attributes.calculateCrowdControlResistance();

        // Resisted effect, cancel and display
        if (resist) {
            noCCTicks = 20;
            new AscendingDisplay(CC_SMALL_CAPS_NAME, 20).display(getLocation());
            return true;
        }

        return false;
    }

    @Nonnull
    public DirectionalMatrix getLookAlongMatrix() {
        return new DirectionalMatrix(this);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        // Tick effects
        entityData.getGameEffects().values().forEach(ActiveGameEffect::tick);

        // Tick dots
        entityData.getDotMap().values().forEach(DotInstanceList::tick);

        aliveTicks++;
        noCCTicks = noCCTicks < 0 ? 0 : noCCTicks - 1;

        // In water ticks
        if (entity.isInWater()) {
            inWaterTicks++;
        }
        else {
            inWaterTicks = 0;
        }
    }

    /**
     * Gets the number of ticks this entity has been in water for, 0 if not in water.
     *
     * @return the number of ticks this entity has been in water for, 0 if not in water.
     */
    public int getInWaterTicks() {
        return inWaterTicks;
    }

    public void clearTitle() {
        asPlayer(Player::resetTitle);
    }

    @Nonnull
    public EntityEquipment getEquipment() {
        final EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) {
            throw new IllegalStateException(getName() + " does not have equipment!");
        }

        return equipment;
    }

    public void lookAt(@Nonnull Location location) {
        CFUtils.lookAt(entity, location);
    }

    public void asNonPlayer(@Nonnull Consumer<LivingGameEntity> consumer) {
        if (entity instanceof Player) {
            return;
        }

        consumer.accept(this);
    }

    /**
     * Launches a projectile from the entity's eye position.
     * <p>
     * This method calls {@link me.hapyl.fight.event.custom.ProjectilePostLaunchEvent}.
     *
     * @param clazz    - Projectile class.
     * @param consumer - Consumer.
     * @return the launched projectile.
     */
    @Nonnull
    public <T extends Projectile> T launchProjectile(@Nonnull Class<T> clazz, @Nullable Consumer<T> consumer) {
        final T projectile = entity.launchProjectile(clazz);

        if (consumer != null) {
            consumer.accept(projectile);
        }

        //new ProjectilePostLaunchEvent(this, projectile).call(); No need to call the event manually anymore
        return projectile;
    }

    @Nonnull
    public <T extends Projectile> T launchProjectile(@Nonnull Class<T> clazz) {
        return launchProjectile(clazz, null);
    }

    @Nonnull
    public Attributes getBaseAttributes() {
        return attributes.getBaseAttributes();
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

    public void setGlowing(@Nonnull GamePlayer player, @Nonnull ChatColor color, int duration) {
        Glowing.glow(entity, color, duration, player.getPlayer());
    }

    public void setGlowing(@Nonnull GamePlayer player, @Nonnull ChatColor color) {
        Glowing.glowInfinitely(entity, color, player.getPlayer());
    }

    public void setGlowingColor(@Nonnull GamePlayer player, @Nonnull ChatColor color) {
        final Glowing glowing = EternaPlugin.getPlugin().getRegistry().glowingManager.getGlowing(player.getPlayer(), entity);

        if (glowing != null) {
            glowing.setColor(color);
        }
        else {
            setGlowing(player, color);
        }
    }

    public void stopGlowing(@Nonnull GamePlayer player) {
        Glowing.stopGlowing(player.getPlayer(), entity);
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
        heal(amount, null);
    }

    public boolean heal(double amount, @Nullable LivingGameEntity healer) {
        final EntityAttributes attributes = getAttributes();

        if (healer != null && !healer.equals(this)) {
            // Don't increase "outgoing" healing towards yourself
            amount = healer.getAttributes().calculateOutgoingHealing(amount);
        }

        amount = attributes.calculateIncomingHealing(amount);

        final double maxHealth = getMaxHealth();
        final double healthAfterHealing = Numbers.clamp(health + amount, getMinHealth(), maxHealth);
        final double actualHealing = healthAfterHealing - health;
        final double excessHealing = Math.max(health + amount - maxHealth, 0);

        if (new GameEntityHealEvent(
                this,
                healer,
                amount,
                actualHealing,
                excessHealing,
                health,
                healthAfterHealing
        ).callAndCheck()) {
            return false;
        }

        // Heal
        this.health = healthAfterHealing;

        // Fx
        if (actualHealing >= 1) {
            PlayerLib.spawnParticle(
                    entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                    Particle.HEART,
                    (int) Numbers.clamp(actualHealing / 5, 1, 10),
                    0.44, 0.2, 0.44, 0.015f
            );

            new AscendingDisplay("&a+ &l%.0f".formatted(actualHealing), 15).display(getLocation());
        }

        return true;
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
            new DamageDisplay(instance).display(getLocation());
        }
    }

    public boolean isAlive() {
        return state == EntityState.ALIVE;
    }

    public Map<Effects, ActiveGameEffect> getActiveEffects() {
        return entityData.getGameEffects();
    }

    public void addDot(@Nonnull DamageOverTime dot, @Nonnull LivingGameEntity damager, int duration) {
        entityData.addDot(dot, duration, damager);
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

        // Don't kill if entity shouldn't die
        if (!shouldDie()) {
            return;
        }

        cooldown.stopCooldowns();
        state = EntityState.DEAD;
        onDeath();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDeath() {
        super.onDeath();
        memory.forgetEverything();
    }

    /**
     * Returns true if this entity is immune to this {@link EnumDamageCause}.
     *
     * @param cause - Cause.
     * @return true if this entity is immune to this cause, false otherwise.
     */
    public boolean isImmune(@Nonnull EnumDamageCause cause) {
        return immunityCauses.contains(cause);
    }

    /**
     * Sets this entity being immune to the given {@link EnumDamageCause}.
     *
     * @param causes - Causes.
     */
    public void setImmune(@Nonnull EnumDamageCause... causes) {
        Collections.addAll(immunityCauses, causes);
    }

    /**
     * Unsets this entity being immune to the given {@link EnumDamageCause}.
     *
     * @param causes - Causes.
     */
    public void unsetImmune(@Nonnull EnumDamageCause... causes) {
        for (EnumDamageCause cause : causes) {
            immunityCauses.remove(cause);
        }
    }

    /**
     * Sets if last damager should be informed about immune damage.
     *
     * @param informImmune - Should be informed.
     * @see #isImmune(EnumDamageCause)
     * @see #setImmune(EnumDamageCause...)
     */
    public void setInformImmune(boolean informImmune) {
        this.informImmune = informImmune;
    }

    @PreprocessingMethod
    public final void onDamageTaken0(@Nonnull DamageInstance instance) {
        final EnumDamageCause cause = instance.getCause();

        if (cause != null && immunityCauses.contains(cause)) {
            final GameEntity damager = instance.getDamager();

            if (damager != null && informImmune) {
                damager.sendMessage(ChatColor.RED + getNameUnformatted() + " is immune to this kind of damage!");
            }

            instance.setCancelled(true);
            return;
        }

        onDamageTaken(instance);
    }

    @Event
    public void onDamageTaken(@Nonnull DamageInstance instance) {
    }

    @PreprocessingMethod
    public final void onDamageDealt0(DamageInstance instance) {
        onDamageDealt(instance);
    }

    @Event
    public void onDamageDealt(@Nonnull DamageInstance instance) {
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

    public boolean isTeammate(@Nullable GameEntity entity) {
        return entity != null && GameTeam.isTeammate(Entry.of(this), Entry.of(entity));
    }

    @Override
    public String toString() {
        return "LivingGameEntity{" + entity.getType() + "@" + entity.getUniqueId() + "}";
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

    @Deprecated
    @Nullable
    public PotionEffect getPotionEffect(@Nonnull PotionEffectType type) {
        return entity.getPotionEffect(type);
    }

    /**
     * Adds a potion effect to this entity.
     *
     * @param effect - Effect to add.
     * @deprecated prefer {@link Effects}.
     */
    @Deprecated
    public void addPotionEffect(@Nonnull PotionEffect effect) {
        entity.addPotionEffect(effect);
    }

    /**
     * Adds a potion effect to this entity.
     *
     * @param type      - Type.
     * @param amplifier - Amplifier.
     * @param duration  - Duration.
     * @deprecated prefer {@link Effects}.
     */
    @Deprecated
    public void addPotionEffect(@Nonnull PotionEffectType type, int amplifier, int duration) {
        addPotionEffect(type.createEffect(duration, amplifier));
    }

    /**
     * Add a potion effect to this entity with infinite duration.
     *
     * @param type      - Effect type.
     * @param amplifier - Amplifier.
     * @deprecated prefer {@link Effects}.
     */
    @Deprecated
    public void addPotionEffectIndefinitely(@Nonnull PotionEffectType type, int amplifier) {
        addPotionEffect(type, amplifier, PotionEffect.INFINITE_DURATION);
    }

    /**
     * Removes a potion effect from this entity.
     *
     * @param type - Effect type.
     * @deprecated prefer {@link Effects}.
     */
    @Deprecated
    public void removePotionEffect(@Nonnull PotionEffectType type) {
        entity.removePotionEffect(type);
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
    public LivingGameEntity getTargetEntity() {
        if (!(this.entity instanceof Creature creature)) {
            return null;
        }

        final LivingEntity target = creature.getTarget();
        return target != null ? CF.getEntity(target) : null;
    }

    public void setTarget(@Nullable LivingGameEntity entity) {
        if (this.entity instanceof Creature creature) {
            creature.setTarget(entity == null ? null : entity.getEntity());
        }
    }

    /**
     * Spawns a particle at this entity location for everyone to see.
     *
     * @param particle - Particle.
     * @param amount   - Amount.
     */
    public void spawnWorldParticle(Particle particle, int amount) {
        spawnWorldParticle(particle, amount, 0, 0, 0, 0);
    }
    // this spawns globally

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
        spawnWorldParticle(location, particle, amount, x, y, z, 0.0f, data);
    }

    public <T> void spawnWorldParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed, T data) {
        final World world = location.getWorld();

        if (world == null || !particle.getDataType().isInstance(data)) {
            return;
        }

        world.spawnParticle(particle, location, amount, x, y, z, speed, data);
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

    public <T> void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, T data) {
        asPlayer(player -> {
            if (!particle.getDataType().isInstance(data)) {
                return;
            }

            player.spawnParticle(particle, location, amount, x, y, z, data);
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
        fn.apply(string, duration).display(getMidpointLocation());
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

        playWorldSound(getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);

        for (int i = 0; i < ferocityStrikes; i++) {
            GameTask.runLater(() -> damageFerocity(damage, lastDamager), i * 3 + FEROCITY_HIT_CD);
        }

        startCooldown(Cooldown.FEROCITY);
    }

    public void damageFerocity(double damage, LivingGameEntity lastDamager) {
        if (isDeadOrRespawning()) {
            return;
        }

        // Ferocity knock-back is kinda crazy, using this little hack to remove it.
        setLastDamager(lastDamager);
        damageTick(damage, (LivingGameEntity) null, EnumDamageCause.FEROCITY, 1);

        // Fx
        final Location location = getLocation();

        playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
        playWorldSound(location, Sound.ENTITY_DONKEY_HURT, 1.25f);

        final double eyeHeight = entity.getEyeHeight();

        final double x = randomDouble(0.5d, 1.25d);
        final double z = randomDouble(0.5d, 1.25d);

        Geometry.drawLine(location.clone().add(x, eyeHeight, z), location.clone().subtract(x, 0, z), 0.2d, FEROCITY_PARTICLE_DATA);
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

        new BuffDisplay("&6ᴅᴏᴅɢᴇᴅ", 10).display(fxLocation);
    }

    @Nonnull
    public String getScoreboardName() {
        return uuid.toString();
    }

    // This defaults vanilla attributes and applies the custom ones.
    // YES, I FORGOT TO CALL UPDATE ATTRIBUTE FOR PLAYERS
    protected void applyAttributes() {
        setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d);
        setAttributeValue(Attribute.GENERIC_ATTACK_SPEED, 2.0d);
        setAttributeValue(Attribute.GENERIC_ATTACK_DAMAGE, 1.0d);
        setAttributeValue(Attribute.GENERIC_ARMOR, -100.0d); // Remove armor bars

        updateAttributes();
    }

    private boolean shouldSimulateDamage() {
        return hasPotionEffect(PotionEffectType.LEVITATION);
    }

    private void updateAttributes() {
        attributes.forEach((type, d) -> attributes.triggerUpdate(type));
    }

    private double randomDouble(double origin, double bound) {
        final double value = random.nextDouble(origin, bound);
        return random.nextBoolean() ? value : -value;
    }

    private short randomShort() {
        return (short) (randomDouble(0.0d, 1.0d) * 8192);
    }
}
