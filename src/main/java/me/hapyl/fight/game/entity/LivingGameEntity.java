package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.module.ai.AI;
import me.hapyl.eterna.module.ai.MobAI;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Draw;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.eterna.module.reflect.glow.Glowing;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.InverseOf;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.entity.packet.EntityPacketFactory;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.display.*;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import me.hapyl.fight.util.MapView;
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
import java.util.function.Function;

public class LivingGameEntity extends GameEntity implements Ticking {

    private static final Draw FEROCITY_PARTICLE_DATA = new FerocityFx();
    private static final int FEROCITY_HIT_CD = 9;
    private static final double ACTUAL_ENTITY_HEALTH = 0.1d;
    private static final String CC_SMALL_CAPS_NAME = SmallCaps.format(AttributeType.EFFECT_RESISTANCE.getName());
    private static final Map<Attribute, Double> DEFAULT_ATTRIBUTE_VALUES = Map.of(
            Attribute.KNOCKBACK_RESISTANCE, 0.0d,
            Attribute.ATTACK_SPEED, 2.0d,
            Attribute.ATTACK_DAMAGE, 1.0d,
            Attribute.ARMOR, -100.0d
    );
    private static final String BLOOD_DEBT_CHAR = "&4&l🩸";

    public final EntityRandom random;
    public final EntityTicker ticker;
    protected final EntityData entityData;
    private final Set<DamageCause> immunityCauses = Sets.newHashSet();
    private final EntityCooldown cooldown;
    private final EntityPacketFactory packetFactory;
    private final EntityMemory memory;
    @Nonnull
    protected EntityAttributes attributes;
    @Nonnull
    protected EntityState state;
    protected int overrideNoDamageTicks = -1; // protected to only allow impl to override
    protected double health;
    protected Shield shield;
    protected Decay decay;
    protected BloodDebt bloodDebt;
    private AI ai;
    private boolean informImmune = true;
    private boolean canMove = true;

    public LivingGameEntity(@Nonnull LivingEntity entity) {
        this(entity, new BaseAttributes(entity));
    }

    public LivingGameEntity(@Nonnull LivingEntity entity, @Nonnull BaseAttributes attributes) {
        super(entity);
        this.attributes = new EntityAttributes(this, attributes);
        this.entityData = new EntityData(this);
        this.state = EntityState.ALIVE;
        this.cooldown = new EntityCooldown(this);
        this.packetFactory = new EntityPacketFactory(this);
        this.memory = new EntityMemory(this);
        this.random = new EntityRandom();
        this.ticker = new EntityTicker(this);

        this.health = attributes.getMaxHealth();
        this.bloodDebt = new BloodDebt(this);

        super.base = false;

        // The actual health of the entity is set to 0.1 to remove the weird
        // hearts when it dies, since the health is not actually decreased.
        // Could really make it so entity actually has the health,
        // but I'm pretty sure the max health is 2048, which is wack
        entity.setMaxHealth(ACTUAL_ENTITY_HEALTH);
        entity.setHealth(entity.getMaxHealth());

        // Default no damage cause because we're using the named system now!
        entity.setMaximumNoDamageTicks(0);

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
        return ticker.aliveTicks.getTick();
    }

    /**
     * Gets the {@link AI} of this entity.
     * The {@link AI} is a {@link me.hapyl.eterna.EternaAPI} module that allows to modify entity's AI easily.
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
    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
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
        addEffect(effect, 0, duration);
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
     * @param amplifier - Amplifier (level) of the effect.
     * @param duration  - Duration of the effect.
     *                  -1 for infinite duration.
     *                  <p>
     *                  <i>Keep in mind infinite effects are <b>not</b> removed at death!</i>
     */
    public void addEffect(@Nonnull Effects effect, int amplifier, int duration) {
        entityData.addEffect(effect, amplifier, duration);
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
    public void damage(double damage, @Nullable GameEntity damager, @Nullable DamageCause cause) {
        // Don't reassign the damage if self damage!
        // That's the whole point of the system to
        // award the last damager even if player killed themselves.
        if (damager != null && !this.equals(damager)) {
            entityData.setLastDamager(damager);
        }

        if (cause != null) {
            entityData.setLastDamageCause(cause);
        }

        // Call the damage event
        entityData.wasHit = true; // This tag is VERY important for calculations
        entity.damage(damage, damager == null ? null : damager.getEntity());
        entityData.wasHit = false;
    }

    public void damage(double d, DamageCause cause) {
        damage(d, (LivingGameEntity) null, cause);
    }

    public void damage(double d, GameEntity damager) {
        damage(d, damager, null);
    }

    public void damage(double damage, @Nullable LivingEntity damager, @Nullable DamageCause cause) {
        damage(damage, CF.getEntity(damager), cause);
    }

    public void damageNoKnockback(double damage, @Nonnull LivingGameEntity damager) {
        damageNoKnockback(damage, damager, null);
    }

    public void damageNoKnockback(double damage, @Nonnull LivingGameEntity damager, @Nullable DamageCause cause) {
        setLastDamager(damager);
        damage(damage, cause);
    }

    @Nonnull
    public EntityLocation getEntityLocation() {
        return new EntityLocation(getLocation());
    }

    public void onTeammateDamage(@Nonnull LivingGameEntity lastDamager) {
        lastDamager.sendMessage("&cCannot damage teammates!");
    }

    public void setCollision(@Nonnull EntityUtils.Collision collision) {
        EntityUtils.setCollision(entity, collision);
    }

    public double scaleHealth(double mul) {
        return health * mul;
    }

    public double scaleAttribute(@Nonnull AttributeType attributeType, double mul) {
        final double v = attributes.get(attributeType);
        return v * mul;
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
        player.hideEntity(CF.getPlugin(), entity);
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
        player.showEntity(CF.getPlugin(), entity);
    }

    public void createExplosion(@Nonnull Location location, double explosionRadius, double explosionDamage, @Nullable DamageCause cause) {
        Collect.nearbyEntities(location, explosionRadius).forEach(entity -> {
            double damage = explosionDamage;

            if (isTeammate(entity)) {
                damage /= 3.0d;
            }

            entity.damage(damage, this, cause);
        });

        // Fx
        Geometry.drawCircle(location, explosionRadius, Quality.NORMAL, new WorldParticle(Particle.CRIT));
        Geometry.drawCircle(location, explosionRadius + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANT));

        final int amountScaled = (int) (explosionRadius * 1.5d);
        final double offsetScaled = (explosionRadius - 2) * 0.8d;

        spawnWorldParticle(location, Particle.EXPLOSION, amountScaled, offsetScaled, 0, offsetScaled, 0);
        playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1);
    }

    public boolean isSelfOrTeammate(@Nullable LivingGameEntity victim) {
        return equals(victim) || isTeammate(victim);
    }

    @InverseOf("isSelfOrTeammate")
    public boolean isNotSelfOrTeammate(@Nullable LivingGameEntity victim) {
        return !isSelfOrTeammate(victim);
    }

    public boolean isSelfOrTeammateOrHasEffectResistance(@Nullable LivingGameEntity victim) {
        return isSelfOrTeammate(victim) || (victim != null && victim.hasEffectResistanceAndNotify(this));
    }

    @InverseOf("isSelfOrTeammateOrHasEffectResistance")
    public boolean isNotSelfOrTeammateOrHasEffectResistance(@Nullable LivingGameEntity victim) {
        return !isSelfOrTeammateOrHasEffectResistance(victim);
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

    public boolean hasEffectResistanceAndNotify(@Nullable GameEntity damager) {
        final boolean resist = hasEffectResistanceAndNotify();

        if (!resist) {
            entityData.setLastDamager(damager);
        }

        return resist;
    }

    public boolean hasEffectResistanceAndNotify() {
        if (ticker.noCCTicks.getTick() > 0) {
            return true;
        }

        final boolean resist = attributes.calculateCrowdControlResistance();

        // Resisted effect, cancel and display
        if (resist) {
            ticker.noCCTicks.setInt(20);
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
        // Tick entity data
        entityData.tick();

        // Tick decay
        if (decay != null) {
            decay.tick();

            final double decayAmount = decay.getDecay();

            if (decayAmount <= 0.0d) {
                decay = null;
            }

            // Fx here because wither hits if it's a lot
            addPotionEffect(PotionEffectType.WITHER, 0, 5);
        }

        // Tick shield
        if (shield != null) {
            shield.tick();
        }

        // Tick
        ticker.tick();
    }

    /**
     * Gets the number of ticks this entity has been in water for, 0 if not in water.
     *
     * @return the number of ticks this entity has been in water for, 0 if not in water.
     */
    public int getInWaterTicks() {
        return ticker.inWaterTicks.getTick();
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
    public BaseAttributes getBaseAttributes() {
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

    public void setGlowing(@Nonnull GamePlayer player, @Nonnull ChatColor color, int duration) {
        Glowing.glow(entity, color, duration, player.getPlayer());
    }

    public void setGlowing(@Nonnull GamePlayer player, @Nonnull ChatColor color) {
        Glowing.glowInfinitely(entity, color, player.getPlayer());
    }

    public void setGlowingColor(@Nonnull GamePlayer player, @Nonnull ChatColor color) {
        final Glowing glowing = Eterna.getManagers().glowing.getGlowing(player.getPlayer(), entity);

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

    @Nonnull
    public HealingOutcome heal(double amount) {
        return heal(amount, null);
    }

    /**
     * Heals this {@link LivingGameEntity} by the given amount with a given healer.
     *
     * @param amount - Amount to heal.
     * @param healer - Healer.
     *               If provided, healer's {@link AttributeType#MENDING} will influence healing amount.
     * @return The {@link HealingOutcome}.
     */
    @Nonnull
    public HealingOutcome heal(double amount, LivingGameEntity healer) {
        // Don't increase "outgoing" healing towards yourself
        if (healer != null && !healer.equals(entity)) {
            amount = healer.getAttributes().calculateOutgoingHealing(amount);
        }

        amount = attributes.calculateIncomingHealing(amount);

        // Check for debt
        final double bloodDebtAmount = bloodDebt.amount();

        if (bloodDebtAmount > 0.0d) {
            final double bloodDebtDecrement = Math.max(0, Math.min(amount, bloodDebtAmount));
            amount -= bloodDebtDecrement;

            bloodDebt.decrement(bloodDebtDecrement);

            // Fx
            playSound(Sound.ENTITY_ZOMBIE_INFECT, 2);
            spawnDebuffDisplay("&c- &l%.0f &4%s".formatted(bloodDebtDecrement, BLOOD_DEBT_CHAR), 20);
        }

        final double maxHealth = getMaxHealth();
        final double healthAfterHealing = Math.clamp(health + amount, getMinHealth(), maxHealth);
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
        ).call()) {
            return HealingOutcome.ofCancelled(actualHealing, excessHealing);
        }

        // Heal
        this.health = healthAfterHealing;

        // Fx
        if (actualHealing >= 1) {
            spawnWorldParticle(
                    entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                    Particle.HEART,
                    (int) Math.clamp(actualHealing / 5, 1, 10),
                    0.44, 0.2, 0.44, 0.015f
            );

            new AscendingDisplay("&a+ &l%.0f".formatted(actualHealing), 15).display(entity.getLocation());
        }

        return HealingOutcome.of(actualHealing, excessHealing);
    }

    public final double getFinalHealth() {
        double health = this.health;

        // Decay
        if (decay != null) {
            health -= decay.getDecay();
        }

        // Overheal // TODO

        return Math.max(1, health); // Don't die from decay or overheal
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    @OverridingMethodsMustInvokeSuper
    public void decreaseHealth(@Nonnull DamageInstance instance) {
        if (isDeadOrRespawning()) {
            return; // Don't decrease health if already dead or respawning
        }

        final DamageCause cause = instance.getCause();

        // Shield
        if (shield != null && shield.canShield(cause)) {
            final double damage = instance.getDamage();
            final double damageMitigated = damage * shield.shieldStrength();
            final double damageTaken = Math.max(0, damage - damageMitigated);
            final double capacityAfterHit = shield.takeDamage0(damageMitigated, instance);

            // Always display shield damage
            if (damageMitigated > 0) {
                shield.display(damageMitigated, this.getEyeLocation());
            }

            // Shield took damage
            if (capacityAfterHit > 0) {
                instance.setDamage(0 + damageTaken);
            }
            // Shield broke
            else {
                instance.setDamage(-capacityAfterHit + damageTaken);

                shield.onBreak0();
                shield = null;
            }
        }

        // Process damage
        final double damage = instance.getDamage();
        final boolean willDie = getFinalHealth() - damage <= 0.0d;

        // Make sure the cause actually CAN kill
        // GameDeathEvent here to not decrease health below lethal
        if (willDie) {
            if (cause != null && !cause.hasFlag(DamageFlag.CAN_KILL)) {
                return;
            }

            if (new GameDeathEvent(instance).call()) {
                return;
            }
        }

        this.health -= damage;

        if (willDie) {
            die(true);

            // call skin
            final GamePlayer playerDamager = instance.getDamagerAsPlayer();

            if (playerDamager != null && !playerDamager.equals(this)) {
                playerDamager.callSkinIfHas(skin -> skin.onKill(playerDamager, this));
            }
        }

        // Damage indicator
        displayDamage(instance);
    }

    @Nullable
    public Decay getDecay() {
        return decay;
    }

    public void setDecay(@Nonnull Decay decay) {
        this.decay = decay;

        // Fx
        playSound(Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.5f);
        spawnParticle(getLocation(), Particle.RAID_OMEN, 10, 0.3, 0.6, 0.3, 0.02f);
    }

    public void displayDamage(@Nonnull DamageInstance instance) {
        final double damage = instance.getDamage();

        if (damage < 1.0d || entity instanceof ArmorStand || entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return;
        }

        final DamageCause cause = instance.getCause();

        if (cause != null) {
            new DamageDisplay(instance).display(getLocation());
        }
    }

    public boolean isAlive() {
        return state == EntityState.ALIVE;
    }

    @Nonnull
    public MapView<Effects, ActiveGameEffect> getActiveEffectsView() {
        return MapView.of(entityData.getGameEffects());
    }

    public void dieBy(@Nonnull DamageCause cause) {
        setLastDamageCause(cause);
        die(true);
    }

    @OverridingMethodsMustInvokeSuper
    public void die(boolean force) {
        if (this.health > 0.0d && !force) {
            return;
        }

        // FIXME (Tue, Feb 4 2025 @xanyjl): Make this DEAD but like why doesn't it work for gp?
        state = deathState();

        cooldown.stopCooldowns();
        bloodDebt.reset();

        shield = null;
        decay = null;

        memory.forgetEverything();

        onDeath();
    }

    @Nonnull
    public BloodDebt bloodDebt() {
        return bloodDebt;
    }

    public double bloodDebtAmount() {
        return bloodDebt.amount();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDeath() {
        super.onDeath();
    }

    @EventLike
    public void onInteract(@Nonnull GamePlayer player) {
    }

    /**
     * Returns true if this entity is immune to this {@link DamageCause}.
     *
     * @param cause - Cause.
     * @return true if this entity is immune to this cause, false otherwise.
     */
    public boolean isImmune(@Nonnull DamageCause cause) {
        return immunityCauses.contains(cause);
    }

    /**
     * Sets this entity being immune to the given {@link DamageCause}.
     *
     * @param causes - Causes.
     */
    public void setImmune(@Nonnull DamageCause... causes) {
        Collections.addAll(immunityCauses, causes);
    }

    /**
     * Unsets this entity being immune to the given {@link DamageCause}.
     *
     * @param causes - Causes.
     */
    public void unsetImmune(@Nonnull DamageCause... causes) {
        for (DamageCause cause : causes) {
            immunityCauses.remove(cause);
        }
    }

    public void resetImmune() {
        immunityCauses.clear();
    }

    /**
     * Sets if last damager should be informed about immune damage.
     *
     * @param informImmune - Should be informed.
     * @see #isImmune(DamageCause)
     * @see #setImmune(DamageCause...)
     */
    public void setInformImmune(boolean informImmune) {
        this.informImmune = informImmune;
    }

    @PreprocessingMethod
    public void onDamageTaken0(@Nonnull DamageInstance instance) {
        // Ignore cancelled instance
        if (instance.isCancelled()) {
            return;
        }

        final DamageCause cause = instance.getCause();
        final GameEntity damager = instance.getDamager();

        // Check immunity
        if (cause != null && immunityCauses.contains(cause)) {

            if (damager != null && informImmune) {
                damager.sendMessage(ChatColor.RED + getNameUnformatted() + " is immune to this kind of damage!");
            }

            instance.setCancelled(true);
            return;
        }

        onDamageTaken(instance);

        // Apply attack cooldown
        if (damager instanceof LivingGameEntity livingDamager && cause != null && !cause.hasFlag(DamageFlag.IGNORES_INVULNERABILITY_TICKS_AND_ATTACK_COOLDOWN)) {
            final EntityAttributes attributes = livingDamager.getAttributes();
            final int baseAttackCooldown = cause.isDirectDamage() ? livingDamager.getAttackCooldown() : cause.attackCooldown();
            final int attackCooldown = attributes.calculateAttackCooldown(baseAttackCooldown);

            livingDamager.entityData.startAttackCooldown(cause, attackCooldown);
        }

        // Apply environment cooldown
        if (cause != null && cause.hasFlag(DamageFlag.ENVIRONMENT)) {
            final int noDamageTicks = this.overrideNoDamageTicks != -1 ? this.overrideNoDamageTicks : DamageCause.defaultNoDamageTicks();

            ticker.noEnvironmentDamageTicks.setInt(noDamageTicks);
        }
    }

    public int getAttackCooldown() {
        return DamageCause.defaultAttackCooldown();
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

    @Nonnull
    public DamageCause getLastDamageCause() {
        return entityData.getLastDamageCauseNonNull();
    }

    public void setLastDamageCause(DamageCause lastDamageCause) {
        entityData.setLastDamageCause(lastDamageCause);
    }

    @Nonnull
    public String getHealthFormatted() {
        final double health = Math.max(0, Math.ceil(getFinalHealth()));
        String healthString = decay != null
                ? "&7&l%.0f &8❤".formatted(health)
                : "&c&l%.0f &c❤".formatted(health);

        if (bloodDebt.hasDebt()) {
            healthString += " &4&l%.0f %s".formatted(bloodDebt.amount(), BLOOD_DEBT_CHAR);
        }

        return healthString;
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

    /**
     * @deprecated {@link #getEntityData}
     */
    @Deprecated
    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return attributes.get(AttributeType.MAX_HEALTH);
    }

    public boolean isTeammate(@Nullable GameEntity entity) {
        return entity != null && GameTeam.isTeammate(Entry.of(this), Entry.of(entity));
    }

    public boolean isEnemy(@Nullable LivingGameEntity entity) {
        return entity != null
                && !equals(entity) // explicit self check since isTeammate is false for self-checks
                && !GameTeam.isTeammate(getEntry(), entity.getEntry());
    }

    @Override
    public String toString() {
        return entity.getType() + "~" + entity.getUniqueId();
    }

    public double getMinHealth() {
        return 0.5d;
    }

    public float getWalkSpeed() {
        return (float) getAttributeValue(Attribute.MOVEMENT_SPEED);
    }

    public void setWalkSpeed(double value) {
        setAttributeValue(Attribute.MOVEMENT_SPEED, value);
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
    public EntityData getEntityData() {
        return entityData;
    }

    @Nonnull
    public String getStatusString() {
        return state.string;
    }

    @Deprecated
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
        addPotionEffect(new PotionEffect(type, duration, amplifier, false, false, false));
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
        return getAttributeValue(Attribute.KNOCKBACK_RESISTANCE);
    }

    public void setKnockback(double d) {
        setAttributeValue(Attribute.KNOCKBACK_RESISTANCE, d);
    }

    public void modifyKnockback(double d, @Nonnull Consumer<LivingGameEntity> consumer) {
        final double knockback = getKnockback();

        setKnockback(d);
        consumer.accept(this);
        setKnockback(knockback);
    }

    public void modifyKnockback(@Nonnull Function<Double, Double> fn, @Nonnull Consumer<LivingGameEntity> consumer) {
        modifyKnockback(fn.apply(getKnockback()), consumer);
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

    @Nonnull
    @Override
    public LivingGameEntity getGameEntity() {
        return this;
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

    public void executeFerocity(double damage, @Nullable LivingGameEntity lastDamager, int ferocityStrikes) {
        executeFerocity(damage, lastDamager, ferocityStrikes, false);
    }

    public void executeFerocity(double damage, @Nullable LivingGameEntity damager, int ferocityStrikes, boolean force) {
        if (isInvalidForFerocity()) {
            return;
        }

        if (hasCooldown(Cooldown.FEROCITY) && !force) {
            return;
        }

        playWorldSound(getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);

        for (int i = 0; i < ferocityStrikes; i++) {
            GameTask.runLater(
                    () -> {
                        if (isInvalidForFerocity()) {
                            return;
                        }

                        damageFerocity(damage, damager);
                    }, i * 3 + FEROCITY_HIT_CD
            );
        }

        startCooldown(Cooldown.FEROCITY);
    }

    public void damageFerocity(double damage, @Nullable LivingGameEntity lastDamager) {
        if (isInvalidForFerocity()) {
            return;
        }

        // Ferocity knock-back is kinda crazy, using this little hack to remove it.
        setLastDamager(lastDamager);
        damage(damage, (GameEntity) null, DamageCause.FEROCITY);

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

    public void updateAttributes() {
        attributes.forEach((type, d) -> attributes.triggerUpdate(type));
    }

    /**
     * Triggers a dummy {@link AttributeTemperEvent} where {@link AttributeTemperEvent#isBuff()} results in <code>true</code>.
     */
    public void triggerBuff(@Nonnull LivingGameEntity applier) {
        AttributeTemperEvent.createDummyEvent(this, applier, true).call();
    }

    /**
     * Triggers a dummy {@link AttributeTemperEvent} where {@link AttributeTemperEvent#isBuff()} results in <code>false</code>.
     */
    public void triggerDebuff(@Nonnull LivingGameEntity applier) {
        AttributeTemperEvent.createDummyEvent(this, applier, false).call();
    }

    public boolean isFullHealth() {
        return getHealth() >= getMaxHealth();
    }

    @Nullable
    public Shield getShield() {
        return shield;
    }

    public void setShield(@Nullable Shield shield) {
        if (this.shield != null) {
            this.shield.onRemove0();
        }

        this.shield = shield;

        if (shield != null) {
            shield.onCreate0();
        }
    }

    public void resetAttributeValue(@Nonnull Attribute attribute) {
        setAttributeValue(attribute, DEFAULT_ATTRIBUTE_VALUES.getOrDefault(attribute, 0.0d));
    }

    public void playHurtAnimation(float yaw) {
        getEntity().playHurtAnimation(yaw);
    }

    @Nonnull
    protected EntityState deathState() {
        return EntityState.DEAD;
    }

    protected boolean isInvalidForFerocity() {
        return isDeadOrRespawning();
    }

    // This defaults vanilla attributes and applies the named ones.
    // YES, I FORGOT TO CALL UPDATE ATTRIBUTE FOR PLAYERS
    protected void applyAttributes() {
        resetAttributeValue(Attribute.KNOCKBACK_RESISTANCE);
        resetAttributeValue(Attribute.ATTACK_SPEED);
        resetAttributeValue(Attribute.ATTACK_DAMAGE);
        resetAttributeValue(Attribute.ARMOR); // Remove armor bars

        updateAttributes();
    }

    private boolean shouldSimulateDamage() { // wtf is this
        return hasPotionEffect(PotionEffectType.LEVITATION);
    }

    private double randomDouble(double origin, double bound) {
        final double value = random.nextDouble(origin, bound);
        return random.nextBoolean() ? value : -value;
    }

    private short randomShort() {
        return (short) (randomDouble(0.0d, 1.0d) * 8192);
    }
}
