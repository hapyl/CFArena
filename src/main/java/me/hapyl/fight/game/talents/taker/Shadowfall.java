package me.hapyl.fight.game.talents.taker;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.taker.TakerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Shadowfall extends Talent {
    
    @DisplayField private final double radius = 3;
    @DisplayField private final short spiritualBonesCost = 3;
    
    @DisplayField(percentage = true) private final double damage = 0.2;
    @DisplayField(percentage = true) private final double speedDecrease = -0.25;
    @DisplayField(percentage = true) private final double defenseDecrease = -0.6;
    
    @DisplayField private final int timeSpendInZoneToTakeDamage = Tick.fromSecond(8);
    @DisplayField private final int impairDuration = Tick.fromSecond(2);
    @DisplayField private final int formDuration = 15;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("shadowfall"));
    
    public Shadowfall(@Nonnull Key key) {
        super(key, "Shadowfall");
        
        setDescription("""
                       Consume &f{spiritualBonesCost}&7 %s to create a Shadowfall at your current location for {duration}.
                       
                       The shadows &8enshroud&7 the &cenemies&7 within, blinding them and decreasing their %s.
                       
                       If an enemy stays in the zone for &b{timeSpendInZoneToTakeDamage}&7, shadows consume them and deal &c{damage}&7 of the current health as &cdamage&7.
                       """.formatted(Named.SPIRITUAL_BONES, AttributeType.DEFENSE));
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        
        setDurationSec(20);
        setCooldownSec(40);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final TakerData data = HeroRegistry.TAKER.getPlayerData(player);
        
        if (data.getBones() < spiritualBonesCost) {
            return Response.error("Not enough Spiritual Bones!");
        }
        
        data.remove(spiritualBonesCost);
        new Shadowfell(player);
        
        return Response.ok();
    }
    
    private class Shadowfell extends TickingGameTask {
        
        private final GamePlayer player;
        private final Location location;
        private final Location centre;
        private final Map<LivingGameEntity, Integer> timeSpentInZone;
        private final double radiusIncreasePerTick;
        
        private double theta;
        private double radius;
        
        private Shadowfell(@Nonnull GamePlayer player) {
            this.player = player;
            this.location = player.getLocationAnchored();
            this.centre = BukkitUtils.newLocation(location);
            this.timeSpentInZone = Maps.newHashMap();
            this.radiusIncreasePerTick = Shadowfall.this.radius / formDuration;
            
            runTaskTimer(0, 1);
        }
        
        @Override
        public void run(int tick) {
            if (tick > getDuration()) {
                cancel();
                timeSpentInZone.clear();
                return;
            }
            
            // Form delay
            if (tick < formDuration) {
                radius += radiusIncreasePerTick;
            }
            
            // Affect entities
            Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammateOrHasEffectResistance)
                   .forEach(entity -> {
                       // Impair
                       entity.getAttributes().addModifier(
                               modifierSource, impairDuration, player, modifier -> modifier
                                       .of(AttributeType.SPEED, ModifierType.ADDITIVE, speedDecrease)
                                       .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseDecrease)
                                       .of(AttributeType.KNOCKBACK_RESISTANCE, ModifierType.FLAT, 100)
                       );
                       
                       entity.addEffect(EffectType.DARKNESS, impairDuration);
                       entity.setGlowingFor(player, GlowingColor.BLACK, impairDuration);
                       
                       
                       // Damage
                       final int newValue = timeSpentInZone.compute(entity, Compute.intAdd());
                       
                       if (newValue > timeSpendInZoneToTakeDamage) {
                           final double damage = entity.getHealth() * Shadowfall.this.damage;
                           
                           entity.damageNoKnockback(damage, player, DamageCause.SHADOWFELL);
                           timeSpentInZone.remove(entity);
                           
                           // Fx
                           entity.playSound(Sound.BLOCK_SWEET_BERRY_BUSH_BREAK, 0.0f);
                           entity.playSound(Sound.ENTITY_WITHER_HURT, 0.75f);
                       }
                   });
            
            // Draw
            final double x = Math.sin(theta) * radius;
            final double y = Math.sin(Math.PI * Math.toRadians(tick)) * 0.2 + 0.3;
            final double z = Math.cos(theta) * radius;
            
            spawnParticle(x, y, z);
            spawnParticle(-x, y, z);
            spawnParticle(x, y, -z);
            spawnParticle(-x, y, -z);
            
            for (double d = 0; d < Math.PI * 2; d += Math.PI / 32) {
                final double mX = Math.sin(d) * radius;
                final double mZ = Math.cos(d) * radius;
                
                LocationHelper.offset(
                        location, mX, 0, mZ, () -> {
                            player.spawnWorldParticle(location, Particle.SMOKE, 1);
                        }
                );
            }
            
            // Sfx
            if (modulo(30)) {
                player.playWorldSound(location, Sound.ENTITY_WARDEN_TENDRIL_CLICKS, 0.0f);
                player.playWorldSound(location, Sound.ENTITY_WARDEN_STEP, 0.0f);
            }
            
            theta += Math.PI / 32;
        }
        
        private void spawnParticle(double x, double y, double z) {
            LocationHelper.offset(
                    location, x, y, z, () -> {
                        final Vector vector = centre.toVector().subtract(location.toVector()).normalize();
                        
                        player.spawnWorldParticle(
                                location, Particle.LARGE_SMOKE, 0,
                                vector.getX() * 0.5,
                                0.125,
                                vector.getZ() * 0.5, 0.4f
                        );
                    }
            );
        }
    }
    
}
