package me.hapyl.fight.game.talents.archive.heavy_knight;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.archive.heavy_knight.SwordMaster;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class Updraft extends Talent implements Listener {

    @DisplayField private final Vector pushDownVelocity = new Vector(0.0d, -0.75d, 0.0d);
    @DisplayField private final double radius = 4.0d;
    @DisplayField private final double plungeRadius = 5.0d;
    @DisplayField private final double plungeDamage = 7.5d;
    @DisplayField private final int maxPlungingTime = Tick.fromSecond(10);

    public Updraft() {
        super("Touchdown");

        setDescription("""
                While &nairborne&7, perform a devastating &bplunging&7 attack, dealing &cdamage&7 upon &nlanding&7.
                                
                If there are &cenemies&7 at the &nsame&7 &nheight&7 level as you, push them down.
                """);

        setType(Type.DAMAGE);
        setItem(Material.DRIED_KELP);
        setCooldownSec(8);
        setDuration(21);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (player.isOnGround()) {
            return Response.error("Must be airborne!");
        }

        final Location location = player.getLocation();
        final Vector direction = location.getDirection();

        direction.setY(0.0d);
        location.add(direction.normalize().multiply(2.0d));

        final Set<LivingGameEntity> plungingEnemies = Sets.newHashSet(player);

        plungingEnemies.addAll(Collect.nearbyEntities(location, radius, entity -> !entity.isSelfOrTeammateOrHasEffectResistance(player)));

        new PlayerTickingGameTask(player) {
            @Override
            public void run(int tick) {
                if (tick > maxPlungingTime) {
                    cancel();
                    return;
                }

                final Location playerLocation = player.getLocation();

                plungingEnemies.removeIf(entity -> {
                    return entity.isDeadOrRespawning() || entity.getLocation().distance(playerLocation) > radius;
                });

                if (plungingEnemies.isEmpty()) {
                    return;
                }

                plungingEnemies.forEach(entity -> {
                    entity.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 20, true);
                    entity.setVelocity(pushDownVelocity);
                });

                // Fx
                if (tick == 0 || modulo(5)) {
                    player.spawnWorldParticle(playerLocation, Particle.SWEEP_ATTACK, 1);
                    player.playWorldSound(playerLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.75f);
                }

                // Collision Check
                if (!player.isOnGround()) {
                    return;
                }

                Collect.nearbyEntities(playerLocation, plungeRadius).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    entity.damageNoKnockback(plungeDamage, player, EnumDamageCause.PLUNGE);
                    SwordMaster.addSuccessfulTalent(player, Updraft.this);
                });

                // Fx
                player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.0f);
                player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DAMAGE, 1.25f);
                player.playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, 0.75f);

                Geometry.drawPolygon(playerLocation.add(0, 0.2, 0), 6, plungeRadius, new WorldParticle(Particle.CRIT));

                cancel();
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
