package me.hapyl.fight.game.talents.heavy_knight;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Updraft extends Talent implements Listener {
    
    @DisplayField private final Vector pushDownVelocity = new Vector(0.0d, -0.75d, 0.0d);
    @DisplayField private final double radius = 4.0d;
    @DisplayField private final double plungeRadius = 5.0d;
    @DisplayField private final double plungeDamage = 7.5d;
    @DisplayField private final double dazedDamageMultiplier = 1.5;
    @DisplayField private final int maxPlungingTime = Tick.fromSeconds(10);
    
    public Updraft(@Nonnull Key key) {
        super(key, "Touchdown");
        
        setDescription("""
                       While &fairborne&7, perform a &bplunging&7 attack, dealing AoE &cdamage&7 upon landing.
                       &8&o;;Dazed enemies take more damage.
                       
                       If there are &cenemies&7 at the same height as you, smash them down.
                       """
        );
        
        setType(TalentType.DAMAGE);
        setMaterial(Material.DRIED_KELP);
        
        setCooldownSec(8);
        setDuration(21);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        if (player.isOnGround()) {
            return Response.error("Must be airborne!");
        }
        
        final Location location = player.getLocation();
        final Vector direction = location.getDirection();
        
        direction.setY(0.0d);
        location.add(direction.normalize().multiply(2.0d));
        
        new Touchdown(player);
        
        return Response.OK;
    }
    
    private class Touchdown extends TickingGameTask {
        
        private final GamePlayer player;
        private final Set<LivingGameEntity> plunging;
        
        private Touchdown(GamePlayer player) {
            this.player = player;
            this.plunging = Sets.newHashSet(Collect.nearbyEntities(
                    player.getLocation(),
                    radius,
                    entity -> entity.equals(player) || !entity.isSelfOrTeammateOrHasEffectResistance(player)
            ));
            
            runTaskTimer(0, 1);
        }
        
        @Override
        public void run(int tick) {
            // Just end the task, don't do the damage
            if (tick > maxPlungingTime) {
                cancel();
                return;
            }
            
            final Location location = player.getLocation();
            
            // If the player is not on the ground, assume we're plunging
            if (!player.isOnGround()) {
                plunging.removeIf(entity -> {
                    return entity.isDeadOrRespawning() || entity.getLocation().distance(location) > radius;
                });
                
                plunging.forEach(entity -> {
                    entity.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 20);
                    entity.setVelocity(pushDownVelocity);
                });
                
                // Fx
                if (tick == 0 || modulo(5)) {
                    player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);
                    player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.75f);
                }
                return;
            }
            
            // Else means we landed, deal damage
            touchdown();
        }
        
        public void touchdown() {
            this.cancel();
            
            final Location location = player.getLocation();
            
            Collect.nearbyEntities(location, plungeRadius, player::isNotSelfOrTeammate).forEach(entity -> {
                final double damage = plungeDamage * (entity.hasEffect(EffectType.DAZE) ? dazedDamageMultiplier : 1);
                
                entity.damageNoKnockback(damage, player, DamageCause.PLUNGE);
                HeroRegistry.SWORD_MASTER.addSuccessfulTalent(player, Updraft.this);
            });
            
            // Fx
            player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.0f);
            player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DAMAGE, 1.25f);
            player.playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);
            
            Geometry.drawPolygon(location.add(0, 0.2, 0), 6, plungeRadius, new WorldParticle(Particle.CRIT));
        }
    }
}
