package me.hapyl.fight.game.talents.bounty_hunter;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.dot.DotType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

public class ShortyShotgun extends ChargedTalent {
    
    @DisplayField(suffix = " blocks") private final double bleedThreshold = 1.0;
    @DisplayField private final int impairDuration = 100;
    @DisplayField private final short bleedStacks = 6;
    
    @DisplayField private final short pellets = 12;
    @DisplayField private final double maxDamagePerPellet = 5.0;
    @DisplayField private final double minDamagePerPellet = 1.0;
    @DisplayField private final double onHookMultiplier = 1.5;
    
    @DisplayField private final double horizontalSpread = 0.5;
    @DisplayField private final double verticalSpread = 0.5;
    
    @DisplayField(suffix = " blocks") private final double maxDistance = 3;
    
    @DisplayField private final int reloadTime = 60;
    
    public ShortyShotgun(@Nonnull Key key) {
        super(key, "Shorty", 2);
        
        setDescription("""
                       Shoot you double barrel to deal &cdamage&7 in small &cAoE&7 in front of you.
                       &8&o;;If used while on Grapple Hook, the damage is increased.
                       
                       If you hit &cenemy&7 point-blank, apply %s and %s for &b{impairDuration}&7.
                       
                       &8&o;;This talent can be used %s consecutively before reloading.
                       """.formatted(DotType.BLEED, EffectType.VULNERABLE, CFUtils.toWordCount(maxCharges()))
        );
        
        setMaterial(Material.CROSSBOW);
        setType(TalentType.DAMAGE);
        
        internalCooldown(2);
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull GamePlayer player, int charges) {
        final Set<Set<PelletResult>> pelletResults = Sets.newHashSet();
        
        for (int i = 0; i < pellets; i++) {
            pelletResults.add(rayCastPellet(player));
        }
        
        // Process results
        pelletResults.forEach(set -> set.forEach(result -> {
            final LivingGameEntity entity = result.entity;
            
            entity.damage(result.damage, player, DamageCause.SHOTGUN);
            
            // Apply bleed
            if (result.isPointBlank) {
                entity.addDotStacks(DotType.BLEED, bleedStacks, player);
                entity.addEffect(EffectType.VULNERABLE, impairDuration, player);
            }
            
            // Knockback
            entity.setVelocity(player.getDirection().normalize().multiply(1.2).setY(0.25));
        }));
        pelletResults.clear();
        
        // Fx
        player.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 1.75f);
        
        return Response.OK;
    }
    
    @Override
    public void onLastCharge(@Nonnull GamePlayer player) {
        rechargeAll(player, reloadTime);
    }
    
    private Set<PelletResult> rayCastPellet(@Nonnull GamePlayer player) {
        final Set<PelletResult> pelletResult = Sets.newHashSet();
        
        final Location location = player.getEyeLocation().subtract(0, 0.2, 0);
        final Vector vector = location.getDirection().normalize().add(getRandomVector(player));
        
        for (double distance = 0; distance < maxDistance; distance += 0.25) {
            final double x = vector.getX() * distance;
            final double y = vector.getY() * distance;
            final double z = vector.getZ() * distance;
            
            // Calculate damage
            double damage = maxDamagePerPellet - ((maxDamagePerPellet - minDamagePerPellet) * (distance / maxDistance));
            
            // If on grapple, multiply the damage
            if (HeroRegistry.BOUNTY_HUNTER.getPlayerData(player).hook != null) {
                damage *= onHookMultiplier;
            }
            
            location.add(x, y, z);
            
            // Affect entities
            for (LivingGameEntity entity : Collect.nearbyEntities(location, 1.0, player::isNotSelfOrTeammate)) {
                pelletResult.add(new PelletResult(entity, damage, distance <= bleedThreshold));
            }
            
            // Fx - Only render after 0.5 distance because you can't see shit
            if (distance >= 0.5) {
                player.spawnWorldParticle(
                        location, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0f, new Particle.DustTransition(
                                Color.fromRGB(0, 4, 10),
                                Color.fromRGB(37, 38, 38),
                                1
                        )
                );
            }
            
            location.subtract(x, y, z);
        }
        
        return pelletResult;
    }
    
    private Vector getRandomVector(GamePlayer player) {
        return new Vector(
                player.random.nextDoubleBool(horizontalSpread),
                player.random.nextDoubleBool(verticalSpread),
                player.random.nextDoubleBool(horizontalSpread)
        );
    }
    
    private record PelletResult(@Nonnull LivingGameEntity entity, double damage, boolean isPointBlank) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            
            final PelletResult that = (PelletResult) o;
            return Objects.equals(this.entity, that.entity);
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.entity);
        }
    }
    
}
