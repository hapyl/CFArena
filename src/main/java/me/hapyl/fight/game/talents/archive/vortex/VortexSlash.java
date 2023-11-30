package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class VortexSlash extends Talent {

    @DisplayField private final int maxDuration = Tick.fromSecond(10);
    @DisplayField private final double damage = 1.0d;
    @DisplayField private final double collectDistance = 2.0d;
    @DisplayField private final double shiftDistance = 0.5;

    public VortexSlash() {
        super("Astral Slash");

        setDescription("""
                Launch an &eastral&7 energy forward &b&nfollows&7 your crosshair.
                                
                &8;;The energy will disappear after {maxDuration} or upon contact with a block.
                                
                The energy deals &crapid damage&7, and knocks enemies back.
                                
                &8;;The cooldown of this ability stars after the energy disappears.
                """);

        setType(Type.DAMAGE);
        setItem(Material.BONE_MEAL);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();

        new TimedGameTask(maxDuration) {

            @Override
            public void onLastTick() {
                startCd(player);
            }

            @Override
            public void run(int tick) {
                final Location nextLocation = location.add(player.getEyeLocation().getDirection().multiply(shiftDistance));

                // Collision check
                if (nextLocation.getBlock().getType().isOccluding()) {
                    startCd(player);
                    cancel();
                    return;
                }

                // Damage
                Collect.nearbyEntities(nextLocation, collectDistance).forEach(entity -> {
                    if (entity.equals(player)) {
                        return;
                    }

                    entity.damageTick(damage, player, EnumDamageCause.SOTS, 0);
                });

                // Fx
                player.spawnWorldParticle(nextLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);

                if (tick % 5 == 0) {
                    PlayerLib.playSound(nextLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                }
            }
        }.runTaskTimer(0, 1);

        startCdInferentially(player);

        return Response.AWAIT;
    }

}
