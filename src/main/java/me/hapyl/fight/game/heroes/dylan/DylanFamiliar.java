package me.hapyl.fight.game.heroes.dylan;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Pet;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.dylan.DylanPassive;
import me.hapyl.fight.game.talents.dylan.SummonWhelp;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DylanFamiliar implements Ticking, Removable {
    
    private static final ItemStack handItem = new ItemStack(Material.SHORT_DRY_GRASS);
    private static final ModifierSource modifierSource = new ModifierSource(Key.ofString("whelp_buff"), true);
    
    private final GamePlayer player;
    private final FamiliarEntity entity;
    
    private SelfDestructState selfDestruct;
    @Nonnull private FamiliarAction action;
    
    private boolean isMoving;
    private int actionDuration;
    
    private int scorch;
    
    public DylanFamiliar(@Nonnull GamePlayer player, @Nonnull SummonWhelp summonWhelp) {
        this.player = player;
        this.action = FamiliarAction.followDylan(player);
        this.actionDuration = Constants.INFINITE_DURATION;
        
        final Location spawnLocation = player.getLocation().add(0, 6, 0);
        this.entity = player.spawnAlliedEntity(
                // Spawn above player so there is a "easy-in" animation idk
                spawnLocation, Entities.VEX, self -> {
                    self.setAI(false);
                    self.setLimitedLifetime(false);
                    self.getEquipment().setItemInMainHand(handItem);
                    
                    final BaseAttributes attribute = new BaseAttributes();
                    attribute.setAttack(player.getAttributes().getAttack()); // Copy attack
                    attribute.setMaxHealth(player.getMaxHealth() * summonWhelp.whelpHealth);
                    attribute.setDefense(100);
                    attribute.setCritChance(0);
                    attribute.setKnockbackResistance(100);
                    
                    return new FamiliarEntity(player, self, attribute);
                }
        );
        
        //  Fx
        player.playWorldSound(spawnLocation, Sound.ENTITY_VEX_CHARGE, 0.75f);
        
        player.spawnWorldParticle(spawnLocation, Particle.FLASH, 1, 0, 0, 0, Color.BLUE);
        player.spawnWorldParticle(spawnLocation, Particle.LAVA, 5, 0.3, 0.3, 0.3, 0.025f);
    }
    
    public boolean isMoving() {
        return isMoving;
    }
    
    @Nonnull
    public FamiliarAction action() {
        return action;
    }
    
    public boolean action(@Nonnull FamiliarAction action, int duration) {
        if (!this.action.isInterruptible()) {
            return false;
        }
        
        this.action = action;
        this.actionDuration = duration;
        this.isMoving = true; // Force movement
        
        return true;
    }
    
    @Nonnull
    public FamiliarEntity entity() {
        return entity;
    }
    
    @Override
    public void remove() {
        this.entity.remove();
    }
    
    @Override
    public void tick() {
        // Don't do anything if self-destructing
        if (selfDestruct != null) {
            return;
        }
        
        final DylanPassive passive = TalentRegistry.DYLAN_PASSIVE;
        final EntityAttributes attributes = entity.getAttributes();
        
        // Update scorch stacks
        if (scorch == 0) {
            attributes.removeModifier(modifierSource);
        }
        else {
            attributes.addModifier(
                    modifierSource, Constants.INFINITE_DURATION, modifier -> modifier
                            .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, passive.attackIncreasePerStack * scorch)
                            .of(AttributeType.MAX_HEALTH, ModifierType.MULTIPLICATIVE, passive.maxHealthIncreasePerStack * scorch)
            );
        }
        
        // Sync location
        final Location anchor = action.destination();
        final Location location = entity.getLocation();
        
        final double distance = anchor.distanceSquared(location);
        
        if (isMoving) {
            // Move until very close
            if (distance > 0.1) {
                final Vector vector = anchor.toVector().subtract(location.toVector()).normalize();
                final double interpolation = Math.min(0.5, Math.sqrt(distance) / 5 * 0.5);
                vector.multiply(interpolation);
                
                // Look towards destination
                final float yaw = (float) Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
                location.setYaw(yaw);
                
                entity.teleport(location.add(vector));
            }
            // Else stop moving
            else {
                isMoving = false;
            }
        }
        else {
            // Start moving if far away
            if (distance > 3) {
                isMoving = true;
            }
            // Else idle
            else {
                final double y = Math.sin(Math.toRadians(entity.aliveTicks()) * 10) * 0.02;
                
                location.add(0, y, 0);
                entity.teleport(location);
            }
        }
        
        // Tick action
        action.tick(player, this);
        
        // Go back to dylan
        if (actionDuration != Constants.INFINITE_DURATION && actionDuration-- <= 0) {
            addBuff();
            returnToDylan();
        }
        
        // Always AGRY
        entity.getEntity().setCharging(true);
    }
    
    public void selfDestruct(@Nonnull SelfDestructState state) {
        // don't allow downgrading the state!
        if (selfDestruct != null && selfDestruct.ordinal() > state.ordinal()) {
            return;
        }
        
        selfDestruct = state;
        selfDestruct.apply(this);
    }
    
    @Nullable
    public SelfDestructState selfDestruct() {
        return selfDestruct;
    }
    
    public void resetBuff() {
        this.scorch = 0;
    }
    
    public void addBuff() {
        this.scorch = Math.clamp(this.scorch + 1, 0, TalentRegistry.DYLAN_PASSIVE.maxScorchStacks);
    }
    
    public double actionDuration() {
        return actionDuration;
    }
    
    private void returnToDylan() {
        this.action = FamiliarAction.followDylan(player);
        this.actionDuration = Constants.INFINITE_DURATION;
        this.isMoving = true;
    }
    
    @Override
    public String toString() {
        return "&4\uD83D\uDC7E &c%s  &6&l%s %s  %s".formatted(
                entity.getHealthFormatted(),
                (scorch == 0 ? "❌" : RomanNumber.toRoman(scorch)), Named.SCORCH.getPrefixColoredBukkit(),
                actionDuration == Constants.INFINITE_DURATION ? "" : "&b&l%s &b⌛".formatted(Tick.format(actionDuration))
        );
    }
    
    public enum SelfDestructState {
        PREPARE {
            @Override
            public void apply(@Nonnull DylanFamiliar familiar) {
                familiar.entity.setInvulnerable(true);
            }
        },
        COMBUST {
            @Override
            public void apply(@Nonnull DylanFamiliar familiar) {
                // Change glowing to red
                familiar.player.getTeam()
                               .getPlayers()
                               .forEach(player -> familiar.entity.setGlowingFor(player, GlowingColor.RED));
            }
        },
        SELF_DESTRUCT {
            @Override
            public void apply(@Nonnull DylanFamiliar familiar) {
                final FamiliarEntity entity = familiar.entity;
                final GamePlayer player = familiar.player;
                final Dylan.DylanUltimate ultimate = HeroRegistry.DYLAN.getUltimate();
                
                final Location location = entity.getLocation();
                
                final double damage = entity.getHealth() * ultimate.damageOfHealth;
                final double damagePerInstance = damage / ultimate.damageInstances;
                
                for (int i = 0; i < ultimate.damageInstances; ++i) {
                    // Offset the location randomly
                    final double x = player.random.nextDoubleBool(ultimate.maxSpreadDistance);
                    final double z = player.random.nextDoubleBool(ultimate.maxSpreadDistance);
                    
                    final Location instanceLocation = LocationHelper.addAsNew(location, x, 0, z);
                    
                    GameTask.runLater(() -> damage(familiar.entity, instanceLocation, damagePerInstance, ultimate.instanceRadius), i + 1);
                }
                
                // Fx
                player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
                
                familiar.remove();
            }
            
            private void damage(FamiliarEntity familiar, Location location, double damage, double radius) {
                Collect.nearbyEntities(location, radius, familiar::isNotSelfOrTeammate)
                       .forEach(entity -> {
                           entity.damageNoKnockback(damage, familiar, DamageCause.WHELP_DESTRUCT);
                           
                           // Fx
                       });
                
                // Fx
                familiar.spawnWorldParticle(location, Particle.LAVA, 5);
                
                familiar.playWorldSound(location, Sound.BLOCK_LAVA_POP, 0.75f);
                familiar.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 0.75f);
            }
        };
        
        public void apply(@Nonnull DylanFamiliar familiar) {
            throw new IllegalArgumentException("Not implemented state: " + name());
        }
    }
    
    public static class FamiliarEntity extends LivingGameEntity implements Pet {
        
        private final GamePlayer player;
        
        FamiliarEntity(@Nonnull GamePlayer player, @Nonnull LivingEntity entity, @Nonnull BaseAttributes attributes) {
            super(entity, attributes);
            
            this.player = player;
            
            // Immunity
            setImmune(DamageCause.SUFFOCATION, DamageCause.FALL, DamageCause.FALLING_BLOCK, DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA);
            
            // Always valid
            setValidState(true);
        }
        
        @Override
        public void onRemove() {
            super.onRemove();
            
            HeroRegistry.DYLAN.whelpTalents(player, false);
        }
        
        @Nonnull
        @Override
        public GamePlayer owner() {
            return player;
        }
        
        @Nonnull
        @Override
        public Vex getEntity() {
            return (Vex) super.getEntity();
        }
        
        @Nonnull
        @Override
        public String getName() {
            return Dylan.familiarName;
        }
    }
}
