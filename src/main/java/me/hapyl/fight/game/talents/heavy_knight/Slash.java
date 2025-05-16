package me.hapyl.fight.game.talents.heavy_knight;


import com.google.common.collect.Sets;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.annotate.ClonedBeforeMutation;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class Slash extends Talent {
    
    public final Talent[] perfectSequence = { TalentRegistry.UPPERCUT, TalentRegistry.UPDRAFT, this };
    
    @DisplayField(scale = 0.001) public final long perfectSequenceWindow = 2500;
    @DisplayField public final int perfectSequenceCooldown = Tick.fromSecond(10);
    
    @DisplayField public final int empoweredSlashDuration = Tick.fromSecond(2);
    
    @DisplayField private final double distance = 3.0d;
    @DisplayField private final double damage = 4.0d;
    
    @DisplayField private final double empoweredDamage = 10;
    @DisplayField private final double empoweredHorizontalOffset = 2.0;
    @DisplayField private final double empoweredRadius = 3.0;
    
    @DisplayField private final int effectDuration = Tick.fromSecond(4);
    
    @DisplayField private final double speedReduction = -25;
    @DisplayField(percentage = true) private final double defenseReduction = -0.25;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("power_slash"), true);
    
    public Slash(@Nonnull Key key) {
        super(key, "Break");
        
        setDescription("""
                       Perform a &fslashing&7 attack in front of you, dealing high &cdamage&7 and knocking all &cenemies&7.
                       
                       &6Perfect Sequence
                       If you cast &a%1$s&e ⏩ &a%2$s&e ⏩ &a%3$s&7 in that order, and each talent hits at least one &cenemy&7, &a%3$s&7 becomes empowered.
                       
                       After casting the empowered &a%3$s&7, &6Leap&7 is enhanced further:
                        &8├&7 Pulls nearby enemies upon landing.
                        &8├&7 Applies %4$s to hit enemies.
                        &8└&7 Resets &a%3$s&7 cooldown.
                       
                       &8&o;;This effect can only trigger once every {perfectSequenceCooldown}.
                       """.formatted(perfectSequence[0], perfectSequence[1], perfectSequence[2], EffectType.DAZE)
        );
        
        setType(TalentType.DAMAGE);
        setMaterial(Material.QUARTZ);
        setCooldownSec(8);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize().setY(0.0d);
        
        location.add(direction.multiply(distance));
        
        direction.multiply(0.5d); // perfect distance to dash
        
        final List<LivingGameEntity> entitiesHit = Collect.nearbyEntities(
                location,
                distance,
                entity -> entity.isValid(player)
        );
        
        boolean strongHit = false;
        
        for (LivingGameEntity entity : entitiesHit) {
            if (HeroRegistry.SWORD_MASTER.addSuccessfulTalent(player, this) && !strongHit) {
                strongHit = true;
            }
            
            entity.damageNoKnockback(damage, player, DamageCause.BREAK);
            entity.setVelocity(direction);
        }
        
        if (strongHit) {
            executeEmpoweredSlash(player, location);
            
            HeroRegistry.SWORD_MASTER.empowerWeapon(player);
        }
        
        // Fx
        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 10, distance, 0.5d, distance, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);
        
        return Response.OK;
    }
    
    public void executeEmpoweredSlash(@Nonnull GamePlayer player, @Nonnull @ClonedBeforeMutation Location origin) {
        final Set<LivingGameEntity> affectedEntities = Sets.newHashSet();
        
        final Location locationLeft = origin.clone().add(LocationHelper.getVectorToTheLeft(origin).multiply(empoweredHorizontalOffset));
        final Location locationRight = origin.clone().add(LocationHelper.getVectorToTheRight(origin).multiply(empoweredHorizontalOffset));
        
        new TickingStepGameTask(4) {
            private double theta;
            
            @Override
            public boolean tick(int tick, int step) {
                if (theta > Math.PI * 2) {
                    affectedEntities.clear();
                    return true;
                }
                
                final double x = Math.sin(theta) * empoweredRadius;
                final double y = Math.sin(Math.toRadians(tick) * 5) * 0.1 + 0.75;
                final double z = Math.cos(theta) * empoweredRadius;
                
                affect(locationLeft, x, y, z, true);
                affect(locationRight, x, y, z, false);
                
                theta += Math.PI / 20;
                return false;
            }
            
            private void affect(Location location, double x, double y, double z, boolean inverse) {
                LocationHelper.offset(
                        location, !inverse ? x : -x, y, !inverse ? z : -z, () -> {
                            Collect.nearbyEntities(location, 1.5, player::isNotSelfOrTeammate)
                                   .forEach(entity -> {
                                       if (!affectedEntities.add(entity)) {
                                           return;
                                       }
                                       
                                       entity.damage(empoweredDamage, player, DamageCause.EMPOWERED_BREAK);
                                       
                                       entity.getAttributes().addModifier(
                                               modifierSource, effectDuration, player, modifier -> modifier
                                                       .of(AttributeType.SPEED, ModifierType.FLAT, speedReduction)
                                                       .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseReduction)
                                       );
                                       
                                       player.playWorldSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.25f);
                                   });
                            
                            // Fx
                            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);
                        }
                );
            }
            
        }.runTaskTimer(0, 1);
    }
    
}
