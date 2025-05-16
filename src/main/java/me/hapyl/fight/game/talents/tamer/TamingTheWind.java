package me.hapyl.fight.game.talents.tamer;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TamingTheWind extends InputTalent implements TamerTimed {
    
    @DisplayField private final double impairRadius = 5.0d;
    @DisplayField private final short maxEnemies = 5;
    
    @DisplayField private final double movementSpeed = 0.5;
    
    private final int tickEnemy = 9;
    private final int tickSelf = 15;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("taming_the_wind"), true);
    
    public TamingTheWind(@Nonnull Key key) {
        super(key, "Taming the Wind");
        
        setDescription("""
                       Equip concentrated wind.
                       """
        );
        
        leftData.setAction("Lift Enemies");
        leftData.setDescription("""
                                Lift up to &b{maxEnemies}&7 nearby &cenemies&7 up into the air, &eimpairing&7 their movement.
                                """
        );
        leftData.setType(TalentType.IMPAIR);
        leftData.setDurationSec(2);
        leftData.setCooldownSec(20);
        
        rightData.setAction("Lift Yourself");
        rightData.setDescription("""
                                 Lift yourself up into the air.
                                 
                                 Use the movement keys to move in that direction.
                                 """
        );
        rightData.setType(TalentType.ENHANCE);
        rightData.copyDurationAndCooldownFrom(leftData);
        
        setMaterial(Material.FEATHER);
    }
    
    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final int duration = getDuration(player);
        
        int liftedEnemies = 0;
        
        for (LivingGameEntity entity : Collect.nearbyEntities(location, impairRadius)) {
            if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                continue;
            }
            
            if (liftedEnemies++ >= maxEnemies) {
                break;
            }
            
            new EntityLevitate<>(entity, duration) {
                @Override
                public void onStart() {
                    entity.setVelocity(new Vector(0, 0.75, 0));
                    entity.getAttributes().addModifier(modifierSource, duration, player, modifier -> modifier.of(AttributeType.KNOCKBACK_RESISTANCE, ModifierType.FLAT, 1_000));
                }
                
                @Override
                public void onTick() {
                    if (getTick() >= tickEnemy) {
                        entity.setVelocity(new Vector(0.0d, 0.0d, 0.0d));
                    }
                }
            };
        }
        
        // Fx
        playSwirlFx(location, false);
        player.playWorldSound(location, Sound.ENTITY_WITHER_SHOOT, 0.75f);
        
        return Response.OK;
    }
    
    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final int duration = getDuration(player);
        final double y = location.getY();
        
        new EntityLevitate<>(player, duration) {
            @Override
            public void onStart() {
                player.addEffect(EffectType.LEVITATION, 6, duration);
                
                player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, duration + 20);
                player.getAttributes().addModifier(modifierSource, duration, player, modifier -> modifier.of(AttributeType.KNOCKBACK_RESISTANCE, ModifierType.FLAT, 1_000));
            }
            
            @Override
            @SuppressWarnings("UnstableApiUsage")
            public void onTick() {
                if (getTick() > tickSelf) {
                    final Input input = player.input();
                    
                    double x = 0;
                    double z = 0;
                    
                    if (input.isForward() || input.isBackward()) {
                        final boolean isForward = input.isForward();
                        
                        z = isForward ? movementSpeed : -movementSpeed;
                    }
                    
                    if (input.isLeft() || input.isRight()) {
                        final boolean isLeft = input.isLeft();
                        
                        x = isLeft ? movementSpeed : -movementSpeed;
                    }
                    
                    // Rotate vector
                    final float yaw = (float) Math.toRadians(player.getLocation().getYaw());
                    
                    final double velocityX = x * Math.cos(yaw) - z * Math.sin(yaw);
                    final double velocityZ = x * Math.sin(yaw) + z * Math.cos(yaw);
                    
                    entity.setVelocity(new Vector(velocityX, 0, velocityZ));
                }
                
                // Fx
                final Location location = player.getLocation();
                location.setY(y);
                
                riptide.teleport(location);
            }
            
        };
        
        // Fx
        playSwirlFx(location, true);
        
        return Response.OK;
    }
    
    private void playSwirlFx(Location location, boolean reverse) {
        new TimedGameTask(10) {
            private final int swirls = 16;
            private final double d = impairRadius / maxTick;
            
            private double radius = reverse ? impairRadius : 1.0d;
            private double theta = 0.0d;
            
            @Override
            public void run(int tick) {
                for (int i = 0; i < swirls; i++) {
                    final double x = Math.sin(theta + 0.2 * (i + tick)) * radius;
                    final double z = Math.cos(theta + 0.2 * (i + tick)) * radius;
                    
                    location.add(x, 0, z);
                    PlayerLib.spawnParticle(location, Particle.EFFECT, 1);
                    location.subtract(x, 0, z);
                    
                    theta += (Math.PI * 2 / swirls);
                }
                
                radius = Math.clamp(reverse ? radius - d : radius + d, 1, impairRadius);
            }
        }.runTaskTimer(0, 1);
    }
}
