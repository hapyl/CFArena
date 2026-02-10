package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.component.ComponentListSupplier;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.math.geometry.Draw;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.Important;
import me.hapyl.fight.annotate.InverseOf;
import me.hapyl.fight.annotate.PreprocessingMethod;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.AttributeModifyEvent;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.dot.Dot;
import me.hapyl.fight.game.dot.DotInstance;
import me.hapyl.fight.game.effect.*;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.entity.cooldown.EntityCooldown;
import me.hapyl.fight.game.entity.cooldown.EntityCooldownHandler;
import me.hapyl.fight.game.entity.packet.EntityPacketFactory;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import me.hapyl.fight.util.MapView;
import net.minecraft.network.protocol.Packet;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class LivingGameEntity extends GameEntity implements Ticking {
    
    public static final EntityCooldown FEROCITY_COOLDOWN = EntityCooldown.of("ferocity", 100L);
    public static final EntityCooldown INTERACT_COOLDOWN = EntityCooldown.of("interact_cooldown", 50L);
    public static final EntityCooldown DECAY_COOLDOWN = EntityCooldown.of("decay_cooldown", 500L);
    
    private static final Draw FEROCITY_PARTICLE_DATA = new FerocityFx();
    private static final int FEROCITY_HIT_CD = 9;
    private static final long ASSIST_DURATION = TimeUnit.SECONDS.toMillis(10);
    private static final double ACTUAL_ENTITY_HEALTH = 0.1d;
    private static final String CC_SMALL_CAPS_NAME = "&lᴇꜰꜰᴇᴄᴛ ʀᴇꜱ";
    private static final String BLOOD_DEBT_CHAR = "&4&l🩸";
    
    private static final Map<Attribute, Double> DEFAULT_ATTRIBUTE_VALUES = Map.of(
            Attribute.KNOCKBACK_RESISTANCE, 0d,
            Attribute.ATTACK_SPEED, 2d,
            Attribute.ATTACK_DAMAGE, 1d,
            Attribute.ARMOR, -100d, // To remove vanilla armor bars
            Attribute.ARMOR_TOUGHNESS, 0d,
            Attribute.ATTACK_KNOCKBACK, 0d,
            Attribute.STEP_HEIGHT, 0.6d
    );
    
    public final EntityRandom random;
    public final EntityTicker ticker;
    
    protected final EntityAttributes attributes;
    protected final Map<Effect, ActiveEffect> effects;
    protected final Map<GamePlayer, Double> damageTaken;
    protected final Map<Dot, DotInstance> dotInstanceMap;
    
    private final Map<DamageCause, Integer> attackCooldown;
    private final Cache<GamePlayer> assistingPlayers;
    private final Set<DamageCause> immunityCauses = Sets.newHashSet();
    private final EntityCooldownHandler cooldown;
    private final EntityPacketFactory packetFactory;
    private final EntityMemory memory;
    
    @Nonnull
    protected EntityState state;
    protected double health;
    protected Shield shield;
    protected Decay decay;
    protected BloodDebt bloodDebt;
    
    @Important(value = "Notifies the event that the damage is named, not vanilla.")
    private boolean wasHit;
    private boolean informImmune = true;
    
    @Nullable private Hologram aboveHead;
    @Nullable private Overlay overlay;
    @Nullable private GameEntity lastDamager;
    @Nullable private DamageCause lastDamageCause;
    
    public LivingGameEntity(@Nonnull LivingEntity entity) {
        this(entity, new BaseAttributes().put(AttributeType.MAX_HEALTH, entity.getHealth()));
    }
    
    public LivingGameEntity(@Nonnull LivingEntity entity, @Nonnull BaseAttributes attributes) {
        super(entity);
        this.attributes = new EntityAttributes(this, attributes);
        this.state = EntityState.ALIVE;
        this.cooldown = new EntityCooldownHandler(this);
        this.packetFactory = new EntityPacketFactory(this);
        this.memory = new EntityMemory(this);
        this.random = new EntityRandom();
        this.ticker = new EntityTicker(this);
        
        this.damageTaken = Maps.newHashMap();
        this.effects = Maps.newHashMap();
        this.attackCooldown = Maps.newConcurrentMap(); // why is this concurrent again?
        this.assistingPlayers = Cache.ofSet(LivingGameEntity.ASSIST_DURATION);
        this.dotInstanceMap = Maps.newHashMap();
        
        this.health = attributes.get(AttributeType.MAX_HEALTH);
        this.bloodDebt = new BloodDebt(this);
        
        super.base = false;
        
        // The actual health of the entity is set to 0.1 to remove the weird
        // hearts when it dies, since the health is not actually decreased.
        // Could really make it so entity actually has the health,
        // but I'm pretty sure the max health is 2048, which is wack
        entity.setMaxHealth(ACTUAL_ENTITY_HEALTH);
        entity.setHealth(entity.getMaxHealth());
        entity.setPersistent(true);
        
        // Default no damage cause because we're using the named system now!
        entity.setMaximumNoDamageTicks(0);
        
        applyAttributes();
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
     * Gets the cause of the last taken damage.
     *
     * @return the last cause of the taken damage.
     */
    @Nonnull
    public DamageCause getLastDamageCause() {
        return lastDamageCause != null ? lastDamageCause : DamageCause.ENTITY_ATTACK;
    }
    
    /**
     * Sets the cause of the last-taken damage.
     *
     * @param cause - Cause.
     */
    public void setLastDamageCause(@Nonnull DamageCause cause) {
        this.lastDamageCause = cause;
    }
    
    /**
     * Gets the last damager.
     *
     * @return the last damager.
     */
    @Nullable
    public GameEntity lastDamager() {
        // Validate if the damage is still alive, unless it's a player
        // if (lastDamager != null) {
        //     if (!(lastDamager instanceof GamePlayer) && lastDamager instanceof LivingGameEntity livingDamager && livingDamager.isDead()) {
        //         lastDamager = null;
        //     }
        // }
        
        return lastDamager;
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
     * Removes the effect from this entity.
     *
     * @param type - Type.
     */
    public void removeEffect(@Nonnull Effect type) {
        final ActiveEffect gameEffect = effects.remove(type);
        
        if (gameEffect != null) {
            gameEffect.remove();
        }
    }
    
    /**
     * Returns true if this entity has the given effect.
     *
     * @param type - Type.
     * @return true if this entity has the given effect.
     */
    public boolean hasEffect(@Nonnull Effect type) {
        return effects.containsKey(type);
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
        lastDamageCause = DamageCause.ENTITY_ATTACK;
    }
    
    public void addAssistingPlayer(@Nonnull GamePlayer player) {
        this.assistingPlayers.add(player);
    }
    
    /**
     * Gets a copy of players who has assisted in the last {@link LivingGameEntity#ASSIST_DURATION}.
     *
     * @return a copy of players who has assisted in the last {@link LivingGameEntity#ASSIST_DURATION}.
     */
    @Nonnull
    public Set<GamePlayer> getAssistingPlayers() {
        return new HashSet<>(assistingPlayers);
    }
    
    public boolean hasAttackCooldown(@Nonnull DamageCause cause) {
        final int cooldown = attackCooldown.getOrDefault(cause, 0);
        
        return !cause.isEnvironmentDamage() && cooldown > 0;
    }
    
    public void startAttackCooldown(@Nonnull DamageCause cause, int cooldown) {
        attackCooldown.put(cause, cooldown);
    }
    
    @Nullable
    public DotInstance dotInstance(@Nonnull Dot dot) {
        return dotInstanceMap.get(dot);
    }
    
    public void addDotStacks(@Nonnull Dot dot, int stacks) {
        addDotStacks(dot, stacks, null);
    }
    
    public void addDotStacks(@Nonnull Dot dot, int stacks, @Nullable LivingGameEntity applier) {
        doWorkDot(dot, applier, instance -> instance.incrementStacks(stacks, applier));
    }
    
    public void setDotStacks(@Nonnull Dot dot, int stacks, @Nullable LivingGameEntity applier) {
        doWorkDot(dot, applier, instance -> instance.setStacksMax(stacks, applier));
    }
    
    public boolean hasAboveHead() {
        return aboveHead != null;
    }
    
    public void aboveHead(@Nullable ComponentListSupplier supplier) {
        // Remove armor stand
        if (supplier == null) {
            if (aboveHead != null) {
                aboveHead.destroy();
                aboveHead = null;
            }
            
            return;
        }
        
        // Create armor stand if it doesn't exist
        if (aboveHead == null) {
            aboveHead = Hologram.ofArmorStand(aboveHeadLocation());
            aboveHead.showAll();
        }
        
        aboveHead.setLines(supplier);
    }
    
    @Nonnull
    public Location aboveHeadLocation() {
        return getEyeLocation().add(0.0d, 0.5d, 0.0d);
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
    
    @Nonnull
    public EntityAttributes getAttributes() {
        return attributes;
    }
    
    public void addEffect(@Nonnull Effect type, int amplifier, int duration, @Nullable LivingGameEntity applier) {
        final Type effectType = type.getType();
        
        // Check for effect resistance
        if (effectType == Type.NEGATIVE && hasEffectResistanceAndNotify(applier)) {
            return;
        }
        
        final ActiveEffect effect = effects.get(type);
        
        // If effect already exists, update the remaining duration, applier and call onUpdate()
        if (effect != null) {
            // Don't override stronger effects
            if (amplifier >= effect.amplifier()) {
                effect.amplifier(amplifier);
                effect.applier(applier);
                effect.tick(duration);
                
                effect.effect().onUpdate(effect);
            }
        }
        // Otherwise create effect
        else {
            effects.put(type, new ActiveEffect(this, applier, type, amplifier, duration));
        }
        
        // Trigger buff/de-buff if there is an applier
        if (applier != null) {
            switch (effectType) {
                case POSITIVE -> triggerBuff(applier);
                case NEGATIVE -> triggerDebuff(applier);
            }
        }
    }
    
    /**
     * Adds the given {@link Effect} to the entity.
     *
     * @param effect   - The effect to add.
     * @param duration - The duration of the effect.
     * @see EffectType
     */
    public void addEffect(@Nonnull Effect effect, int duration) {
        addEffect(effect, 0, duration);
    }
    
    /**
     * Adds the given {@link Effect} to the entity.
     *
     * @param effect    - The effect to add.
     * @param amplifier - The amplifier of the effect.
     *                  <i>Only applicable to {@link VanillaEffect}</i>.
     * @param duration  - The duration of the effect.
     * @see EffectType
     */
    public void addEffect(@Nonnull Effect effect, int amplifier, int duration) {
        addEffect(effect, amplifier, duration, null);
    }
    
    /**
     * Adds the given {@link Effect} to the entity.
     *
     * @param effect   - The effect to add.
     *                 <i>Only applicable to {@link VanillaEffect}</i>.
     * @param duration - The duration of the effect.
     * @param applier  - The applier.
     *                 <i>If present, appliers {@link AttributeType#EFFECT_HIT_RATE} will be considered and either a buff or a de-buff will be triggered.</i>
     * @see EffectType
     */
    public void addEffect(@Nonnull Effect effect, int duration, @Nullable LivingGameEntity applier) {
        addEffect(effect, 0, duration, applier);
    }
    
    /**
     * Removes all the effect from this entity with the given type.
     *
     * @param type - Type.
     */
    public void removeEffectsByType(@Nonnull Type type) {
        effects.values().removeIf(
                effect -> {
                    if (!effect.isInfiniteDuration() && effect.effect().getType() == type) {
                        effect.remove();
                        return true;
                    }
                    
                    return false;
                }
        );
    }
    
    /**
     * Kills an entity with the death animation.
     */
    @Override
    public void remove() {
        super.remove();
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
            lastDamager = damager;
        }
        
        if (cause != null) {
            lastDamageCause = cause;
        }
        
        // Call the damage event
        wasHit = true; // This tag is VERY important for calculations
        entity.damage(damage, damager == null ? null : damager.getEntity());
        wasHit = false;
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
    
    public void damageNoKnockback(double damage, @Nullable LivingGameEntity damager) {
        damageNoKnockback(damage, damager, null);
    }
    
    public void damageNoKnockback(double damage, @Nullable LivingGameEntity damager, @Nullable DamageCause cause) {
        setLastDamager(damager);
        damage(damage, cause);
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
        return isVisibleTo(player.getEntity());
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
        if (CF.environment().considerSelfAsEnemy.isEnabled()) {
            return false;
        }
        
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
    
    public boolean hasEffectResistanceAndNotify(@Nullable LivingGameEntity damager) {
        // This isn't a turn-based game, so effect resistance has a 'lingering' 1s window where entity cannot be affected
        if (ticker.noCCTicks.getTick() > 0) {
            return true;
        }
        
        final double effectResistance = attributes.normalized(AttributeType.EFFECT_RESISTANCE);
        final double effectHitRate = damager != null ? damager.getAttributes().normalized(AttributeType.EFFECT_HIT_RATE) : 0;
        
        final double chanceToResist = effectResistance * (1 - effectHitRate);
        final boolean hasResisted = random.checkBound(chanceToResist);
        
        if (hasResisted) {
            ticker.noCCTicks.setInt(20);
            StringDisplay.ascend(getMidpointLocation(), CC_SMALL_CAPS_NAME, 20);
            playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
            return true;
        }
        
        // Set the last damager as the one who applied effect, allowing "killing" with
        // cc abilities like throwing off a map or applying a bad effect that kills
        setLastDamager(damager);
        return false;
    }
    
    public boolean hasEffectResistanceAndNotify() {
        return hasEffectResistanceAndNotify(null);
    }
    
    @Nonnull
    public DirectionalMatrix getLookAlongMatrix() {
        return new DirectionalMatrix(this);
    }
    
    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        // Tick entity data
        final Collection<ActiveEffect> effects = this.effects.values();
        
        effects.removeIf(ActiveEffect::removeIfShould);
        effects.forEach(ActiveEffect::tick);
        
        // Tick dot
        tickDots();
        
        // Tick attack cooldowns
        tickAttackCooldowns();
        
        // Tick decay
        if (decay != null) {
            decay.tick();
            
            final double decayAmount = decay.getDecay();
            
            // Clear decay
            if (decayAmount <= 0.0d) {
                decay = null;
                
                // Fx
                playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.25f);
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
        
        // Tick above head
        if (aboveHead != null) {
            aboveHead.teleport(aboveHeadLocation());
        }
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
    
    public void swingMainHand() {
        entity.swingMainHand();
    }
    
    public void swingOffHand() {
        entity.swingOffHand();
    }
    
    public void equipment(@Nonnull EntityEquipment equipment) {
        final EntityEquipment entityEquipment = getEquipment();
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            entityEquipment.setItem(slot, equipment.getItem(slot));
        }
    }
    
    @Nonnull
    public EntityEquipment getEquipment() {
        return Objects.requireNonNull(entity.getEquipment(), "No equipment!");
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
    
    public void setGlowingFor(@Nonnull GamePlayer player, @Nonnull GlowingColor color, int duration) {
        if (validateNotGlowingForSelf(player)) {
            return;
        }
        
        Glowing.setGlowing(player.getEntity(), entity, color, duration);
    }
    
    public void setGlowingFor(@Nonnull GamePlayer player, @Nonnull GlowingColor color) {
        if (validateNotGlowingForSelf(player)) {
            return;
        }
        
        Glowing.setGlowing(player.getEntity(), entity, color);
    }
    
    public void setGlowingFor(@Nonnull GamePlayer player) {
        if (validateNotGlowingForSelf(player)) {
            return;
        }
        
        Glowing.stopGlowing(player.getEntity(), entity);
    }
    
    @Nonnull
    public Location getLocationAnchored() {
        final Location location = getLocation();
        return GamePlayer.anchorLocation(location);
    }
    
    @Nonnull
    public HealingOutcome healRelativeToMaxHealth(@Range(from = 0, to = 1) double percentage) {
        return healRelativeToMaxHealth(percentage, null);
    }
    
    @Nonnull
    public HealingOutcome healRelativeToMaxHealth(@Range(from = 0, to = 1) double percentage, @Nullable LivingGameEntity healer) {
        return heal(getMaxHealth() * Numbers.clamp01(percentage), healer);
    }
    
    /**
     * @deprecated Prefer healing based on max health using {@link #healRelativeToMaxHealth(double)}.
     */
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
     * @deprecated Prefer healing based on max health using {@link #healRelativeToMaxHealth(double)}.
     */
    @Nonnull
    @Deprecated
    public HealingOutcome heal(double amount, @Nullable LivingGameEntity healer) {
        // Don't increase "outgoing" healing towards yourself
        if (healer != null && !healer.equals(entity)) {
            amount = healer.getAttributes().calculate().outgoingHealing(amount);
        }
        
        amount = attributes.calculate().incomingHealing(amount);
        
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
        ).callEvent()) {
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
            
            StringDisplay.ascend(entity.getLocation(), "&a+ &l%.0f".formatted(actualHealing), 15);
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
            final double damageMitigated = damage * shield.data.strength();
            final double damageTaken = Math.max(0, damage - damageMitigated);
            final double capacityAfterHit = shield.takeDamage0(damageMitigated, instance);
            
            // Always display shield damage
            if (damageMitigated > 0) {
                shield.display(damageMitigated, this.getEyeLocation());
                instance.markShielded();
            }
            
            // Shield took damage
            if (capacityAfterHit > 0) {
                instance.overrideDamage(damageTaken);
            }
            // Shield broke
            else {
                instance.overrideDamage(-capacityAfterHit + damageTaken);
                
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
            if (!cause.hasFlag(DamageFlag.CAN_KILL)) {
                return;
            }
            
            if (new GameDeathEvent(instance).callEvent()) {
                return;
            }
        }
        
        this.health -= damage;
        
        if (this.health <= 0.0d) {
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
        if (hasCooldown(DECAY_COOLDOWN)) {
            return;
        }
        
        this.decay = decay;
        startCooldown(DECAY_COOLDOWN);
        
        // Fx
        playWorldSound(Sound.ENTITY_WITHER_SKELETON_HURT, 0.0f);
        playWorldSound(Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.0f);
        playWorldSound(Sound.ENTITY_VEX_DEATH, 0.75f);
        
        spawnWorldParticle(getMidpointLocation(), Particle.RAID_OMEN, 15, 0.3d, 0.6d, 0.3d, 0.02f);
    }
    
    public void displayDamage(@Nonnull DamageInstance instance) {
        final double damage = instance.getDamage();
        
        if (damage < 1.0d || entity instanceof ArmorStand || entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return;
        }
        
        StringDisplay.damage(getMidpointLocation(), instance);
    }
    
    public boolean isAlive() {
        return state == EntityState.ALIVE;
    }
    
    @Nonnull
    public MapView<Effect, ActiveEffect> getActiveEffectsView() {
        return MapView.of(effects);
    }
    
    @Nonnull
    public MapView<Dot, DotInstance> getDotInstanceView() {
        return MapView.of(dotInstanceMap);
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
        
        state = EntityState.DEAD;
        
        cooldown.stopCooldowns();
        bloodDebt.reset();
        
        if (shield != null) {
            shield.onRemove(Shield.Cause.DEATH);
            shield = null;
        }
        
        decay = null;
        
        memory.forgetEverything();
        
        remove();
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
    public void onRemove() {
        if (aboveHead != null) {
            aboveHead.destroy();
            aboveHead = null;
        }
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
    public final void onDamageTaken0(@Nonnull DamageInstance instance) {
        // Ignore cancelled instance
        if (instance.isCancelled()) {
            return;
        }
        
        final DamageCause cause = instance.getCause();
        final GameEntity damager = instance.getDamager();
        
        // Check immunity
        if (immunityCauses.contains(cause)) {
            
            if (damager != null && informImmune) {
                damager.sendMessage(ChatColor.RED + getName() + " is immune to this kind of damage!");
            }
            
            instance.setCancelled(true);
            return;
        }
        
        onDamageTaken(instance);
        
        // Apply attack cooldown
        if (
                damager instanceof LivingGameEntity livingDamager
                        && !cause.hasFlag(DamageFlag.IGNORES_ICD)
                        && !cause.isEnvironmentDamage()
                        && shouldStartAttackCooldown()
        ) {
            final EntityAttributes attributes = livingDamager.getAttributes();
            final int baseAttackCooldown = cause.isDirectDamage() ? livingDamager.getAttackCooldown() : cause.attackCooldown();
            final int attackCooldown = attributes.calculate().attackCooldown(baseAttackCooldown);
            
            livingDamager.startAttackCooldown(cause, attackCooldown);
        }
        
        // Apply environment cooldown
        if (cause.isEnvironmentDamage() && shouldStartAttackCooldown()) {
            final int noDamageTicks = DamageCause.defaultNoDamageTicks();
            
            ticker.noEnvironmentDamageTicks.setInt(noDamageTicks);
        }
    }
    
    @EventLike
    public void onDamageTaken(@Nonnull DamageInstance instance) {
    }
    
    @PreprocessingMethod
    public final void onDamageDealt0(DamageInstance instance) {
        onDamageDealt(instance);
    }
    
    @EventLike
    public void onDamageDealt(@Nonnull DamageInstance instance) {
    }
    
    public int getAttackCooldown() {
        return DamageCause.defaultAttackCooldown();
    }
    
    @Nonnull
    public String getHealthFormatted() {
        final double health = Math.max(0, Math.ceil(getFinalHealth()));
        String healthString = decay != null
                              ? "&7&l%,.0f &8❤".formatted(health)
                              : "&c&l%,.0f &c❤".formatted(health);
        
        if (bloodDebt.hasDebt()) {
            healthString += " &4&l%,.0f %s".formatted(bloodDebt.amount(), BLOOD_DEBT_CHAR);
        }
        
        return healthString;
    }
    
    /**
     * Sets the last damager.
     *
     * @param lastDamager - New last damager.
     */
    public void setLastDamager(@Nullable GameEntity lastDamager) {
        this.lastDamager = lastDamager;
    }
    
    public void setLastDamager(LivingGameEntity lastDamager) {
        if (lastDamager != null) {
            this.lastDamager = lastDamager;
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
    
    public void overrideHealth(double newHealth) {
        attributes.setMaxHealth(newHealth);
        health = newHealth;
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
        // ClassName(EntityType)[Id]
        return "%s(%s)[%s]".formatted(this.getClass().getSimpleName(), entity.getType(), entity.getEntityId());
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
        workAttribute(
                attribute, instance -> {
                    instance.setBaseValue(value);
                    return null;
                }, null
        );
    }
    
    public double getAttributeValue(@Nonnull Attribute attribute) {
        return workAttribute(attribute, AttributeInstance::getBaseValue, 0d);
    }
    
    public void resetAttributeValue(@Nonnull Attribute attribute) {
        setAttributeValue(attribute, Objects.requireNonNull(DEFAULT_ATTRIBUTE_VALUES.get(attribute), "unregistered attribute: " + attribute));
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
     */
    public void addPotionEffect(@Nonnull PotionEffect effect) {
        entity.addPotionEffect(effect);
    }
    
    /**
     * Adds a potion effect to this entity.
     *
     * @param type      - Type.
     * @param amplifier - Amplifier.
     * @param duration  - Duration.
     */
    public void addPotionEffect(@Nonnull PotionEffectType type, int amplifier, int duration) {
        addPotionEffect(new PotionEffect(type, duration, amplifier, false, false, false));
    }
    
    /**
     * Add a potion effect to this entity with infinite duration.
     *
     * @param type      - Effect type.
     * @param amplifier - Amplifier.
     */
    public void addPotionEffectIndefinitely(@Nonnull PotionEffectType type, int amplifier) {
        addPotionEffect(type, amplifier, PotionEffect.INFINITE_DURATION);
    }
    
    /**
     * Removes a potion effect from this entity.
     *
     * @param type - Effect type.
     * @deprecated prefer {@link EffectType}.
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
    // this spawns globally
    
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
        StringDisplay.ascend(getMidpointLocation(), string, duration);
    }
    
    public void spawnDebuffDisplay(@Nonnull String string, int duration) {
        StringDisplay.descend(getMidpointLocation(), string, duration);
    }
    
    @Nonnull
    public EntityCooldownHandler getCooldown() {
        return cooldown;
    }
    
    public boolean hasCooldown(@Nonnull EntityCooldown cooldown) {
        return this.cooldown.hasCooldown(cooldown);
    }
    
    public void startCooldown(@Nonnull EntityCooldown cooldown, long duration) {
        this.cooldown.startCooldown(cooldown, duration);
    }
    
    public void startCooldown(@Nonnull EntityCooldown cooldown) {
        this.cooldown.startCooldown(cooldown);
    }
    
    public void executeFerocity(double damage, @Nullable LivingGameEntity lastDamager, int ferocityStrikes) {
        executeFerocity(damage, lastDamager, ferocityStrikes, false);
    }
    
    public void executeFerocity(double damage, @Nullable LivingGameEntity damager, int ferocityStrikes, boolean force) {
        if (isInvalidForFerocity()) {
            return;
        }
        
        if (hasCooldown(FEROCITY_COOLDOWN) && !force) {
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
        
        startCooldown(FEROCITY_COOLDOWN);
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
    
    public void playHurtSound(boolean globally) {
        final Sound hurtSound = getEntity().getHurtSound();
        
        if (hurtSound == null) {
            return;
        }
        
        if (globally) {
            playWorldSound(hurtSound, 1.0f);
        }
        else {
            playSound(hurtSound, 1.0f);
        }
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
        
        StringDisplay.ascend(fxLocation, "&6&lᴅᴏᴅɢᴇᴅ", 10);
    }
    
    @Nonnull
    public String getScoreboardName() {
        return uuid.toString();
    }
    
    public void triggerBuff(@Nonnull LivingGameEntity applier) {
        AttributeModifyEvent.createDummyEvent(this, applier, true).callEvent();
    }
    
    public void triggerDebuff(@Nonnull LivingGameEntity applier) {
        AttributeModifyEvent.createDummyEvent(this, applier, false).callEvent();
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
    
    public void playHurtAnimation(float yaw) {
        getEntity().playHurtAnimation(yaw);
    }
    
    public boolean isNotOnCooldownAndStart(@Nonnull EntityCooldown cooldown) {
        final boolean onCooldown = hasCooldown(cooldown);
        
        if (onCooldown) {
            return false;
        }
        
        startCooldown(cooldown);
        return true;
    }
    
    public void redirectDamage(@Nonnull DamageInstance instance) {
        // Set damage cause and damager
        lastDamageCause = instance.getCause();
        
        final LivingGameEntity damager = instance.getDamager();
        
        if (damager != null) {
            lastDamager = damager;
        }
        
        // Actually decrease health
        decreaseHealth(instance);
        
        // Fx
        playHurtSound(false);
        playHurtAnimation(0.0f);
    }
    
    public void sendCenteredMessage(@Nonnull String message) {
        asPlayer(player -> Chat.sendCenterMessage(player, message));
    }
    
    @Nullable
    public ActiveEffect getActiveEffect(@Nonnull Effect effect) {
        return effects.get(effect);
    }
    
    public void playWorldSoundAtTicks(@Nonnull Location location, @Nonnull Sound sound, float startPitch, float endPitch, @Range(from = 2, to = Integer.MAX_VALUE) int... ticks) {
        doPlaySoundAtTick(startPitch, endPitch, pitch -> playWorldSound(location, sound, pitch), ticks);
    }
    
    public void playSoundAtTicks(@Nonnull Location location, @Nonnull Sound sound, float startPitch, float endPitch, @Range(from = 2, to = Integer.MAX_VALUE) int... ticks) {
        doPlaySoundAtTick(startPitch, endPitch, pitch -> playSound(location, sound, pitch), ticks);
    }
    
    public boolean hasDot(@Nonnull Dot dot) {
        return dotInstanceMap.containsKey(dot);
    }
    
    public int getDotStacks(@Nonnull Dot dot) {
        final DotInstance instance = dotInstanceMap.get(dot);
        
        return instance != null ? instance.stacks() : 0;
    }
    
    public void removeDot(@Nonnull Dot dot) {
        final DotInstance instance = dotInstanceMap.remove(dot);
        
        // If instance is not null means it's not new dot, call onStop
        if (instance != null) {
            dot.onStop(instance);
        }
    }
    
    protected boolean shouldStartAttackCooldown() {
        return true;
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
        
        attributes.updateAttributes();
    }
    
    private void doWorkDot(Dot dot, LivingGameEntity applier, Consumer<DotInstance> consumer) {
        if (hasEffectResistanceAndNotify(applier)) {
            return;
        }
        
        final boolean newDot = !dotInstanceMap.containsKey(dot);
        final DotInstance instance = dotInstanceMap.computeIfAbsent(dot, f -> dot.newInstance(this));
        
        consumer.accept(instance);
        
        // Call onStart
        if (newDot) {
            dot.onStart(instance);
        }
    }
    
    private <T> T workAttribute(Attribute attribute, Function<AttributeInstance, T> fn, T def) {
        final AttributeInstance instance = entity.getAttribute(attribute);
        
        if (instance != null) {
            return fn.apply(instance);
        }
        
        return def;
    }
    
    private void tickDots() {
        final Collection<DotInstance> dot = dotInstanceMap.values();
        final int aliveTicks = aliveTicks();
        
        final Iterator<DotInstance> iterator = dot.iterator();
        
        while (iterator.hasNext()) {
            final DotInstance instance = iterator.next();
            
            // Always tick
            instance.tick();
            
            if (aliveTicks % instance.dot().affectPeriod() == 0) {
                instance.affect();
                
                // Remove right after ticking because it's DoT is stack-based
                if (instance.stacks() <= 0) {
                    iterator.remove();
                    
                    // Call onStop
                    instance.dot().onStop(instance);
                }
            }
        }
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
    
    private void doPlaySoundAtTick(float pitchStart, float pitchEnd, Consumer<Float> cn, int... ticks) {
        Validate.isTrue(pitchStart < pitchEnd, "start pitch must be lower than end pitch");
        Validate.isTrue(ticks.length > 2, "there must be at least two ticks");
        
        final int lastTick = ticks[ticks.length - 1];
        final float pitchIncrease = (pitchEnd - pitchStart) / ticks.length;
        
        new TickingGameTask() {
            private float pitch = pitchStart;
            
            @Override
            public void run(int tick) {
                if (tick > lastTick) {
                    cancel();
                    return;
                }
                
                for (int t : ticks) {
                    if (tick == t) {
                        cn.accept(pitch);
                        pitch += pitchIncrease;
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }
    
    private boolean validateNotGlowingForSelf(GamePlayer player) {
        if (equals(player)) {
            Debug.warn("%s tried to glow for self!".formatted(toString()));
            return true;
        }
        
        return false;
    }
    
    private double randomDouble(double origin, double bound) {
        final double value = random.nextDouble(origin, bound);
        return random.nextBoolean() ? value : -value;
    }
    
    private short randomShort() {
        return (short) (randomDouble(0.0d, 1.0d) * 8192);
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
        final IGameInstance instance = Manager.current().currentInstance();
        
        if (instance instanceof GameInstance) {
            CF.getEntities(LivingGameEntity.class).forEach(LivingGameEntity::resetDamage);
        }
    }
}
