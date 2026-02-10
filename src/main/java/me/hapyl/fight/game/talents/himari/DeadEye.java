package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.SnapshotAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.WarningType;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class DeadEye extends HimariTalent implements Listener {
    
    @DisplayField private final double damage = 10;
    @DisplayField private final double maxDistance = 40;
    @DisplayField private final double defenseIgnore = 80;
    @DisplayField private final double critChanceIncrease = 100;
    
    public DeadEye(@Nonnull Key key) {
        super(key, "Dead Eye");
        
        setDescription("""
                       %s
                       
                       Start charging &cDead Eye&7 on the &etarget&7 enemy for {duration}.
                       
                       When charged, execute a deadly shot that deals %s and ignores &2{defenseIgnore}&7 of victim's %s.
                       &8&o;;Losing the line of sight of the focused enemy ends the dead eye.
                       """.formatted(howToGetString, AttributeType.CRIT_DAMAGE, AttributeType.DEFENSE));
        
        setMaterial(Material.SPECTRAL_ARROW);
        setType(TalentType.DAMAGE);
        setDurationSec(3);
    }
    
    @EventHandler
    public void handleDeadEye(GameDamageEvent.PreProcess ev) {
        final SnapshotAttributes entity = ev.entity();
        final SnapshotAttributes damager = ev.damager();
        
        if (damager == null || !(damager.entity() instanceof GamePlayer)) {
            return;
        }
        
        if (ev.getCause() != DamageCause.DEAD_EYE) {
            return;
        }
        
        damager.add(AttributeType.CRIT_CHANCE, critChanceIncrease);
        entity.subtract(AttributeType.DEFENSE, defenseIgnore);
    }
    
    @Nonnull
    @Override
    public Response executeHimari(@NotNull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityRayCast(player, maxDistance, 1.0d, player::isNotSelfOrTeammate);
        
        if (target == null) {
            return Response.error("No valid target for dead eye!");
        }
        
        new PlayerTickingGameTask(player) {
            @Override
            public void run(int tick) {
                if (tick >= getDuration()) {
                    deadShot();
                    cancel();
                    return;
                }
                
                // Check for line of sight
                if (!player.hasLineOfSight(target)) {
                    cancel();
                    
                    // Fx
                    player.playWorldSound(Sound.ENTITY_PUFFER_FISH_STING, 0.0f);
                    return;
                }
                
                final Location location = player.getLocation();
                final Vector vector = target.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
                
                location.setDirection(vector);
                player.teleport(location);
                
                // Fx
                final int percentDone = (int) (15d * tick / getDuration());
                player.sendSubtitle(("&e&l\uD83D\uDC41".repeat(percentDone) + ("&8&l\uD83D\uDC41".repeat(15 - percentDone))), 0, 5, 0);
                
                // Send warning to the target
                if (target instanceof GamePlayer playerTarget) {
                    playerTarget.sendWarning(WarningType.WARNING, 5);
                }
            }
            
            private void deadShot() {
                target.damage(damage, player, DamageCause.DEAD_EYE);
                
                // Fx
                player.playWorldSound(Sound.ENTITY_ZOMBIE_INFECT, 0.34f);
                player.playWorldSound(Sound.ENTITY_ZOMBIE_HURT, 0.75f);
                
                Geometry.drawLine(
                        player.getEyeLocation(), target.getEyeLocation(), 0.25d, location -> {
                            player.spawnWorldParticle(location, Particle.FLAME, 1);
                            player.spawnWorldParticle(location, Particle.FALLING_LAVA, 1);
                        }
                );
            }
            
        }.runTaskTimer(0, 1);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
        
        return Response.OK;
    }
}
