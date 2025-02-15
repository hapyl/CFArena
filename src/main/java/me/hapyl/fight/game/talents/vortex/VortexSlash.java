package me.hapyl.fight.game.talents.vortex;


import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.player.PlayerTimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class VortexSlash extends Talent {

    @DisplayField private final int maxDuration = Tick.fromSecond(3);
    @DisplayField private final double damage = 1.0d;
    @DisplayField private final double collectDistance = 2.0d;
    @DisplayField private final double shiftDistance = 0.5;

    public VortexSlash(@Nonnull Key key) {
        super(key, "Astral Slash");

        setDescription("""
                Launch an &eastral&7 energy forward &b&nfollows&7 your crosshair.
                
                &8;;The energy will disappear after {maxDuration} or upon contact with a block.
                
                The energy deals &crapid damage&7, and knocks enemies back.
                
                &8;;The cooldown of this ability stars after the energy disappears.
                """
        );

        setType(TalentType.DAMAGE);
        setItem(Material.BONE_MEAL);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getEyeLocation();

        new PlayerTimedGameTask(player, maxDuration) {

            @Override
            public void onLastTick() {
                startCd(player);
            }

            @Override
            public void run(int tick) {
                final Location nextLocation = location.add(player.getEyeLocation().getDirection().multiply(shiftDistance));

                // Collision check
                if (nextLocation.getBlock().getType().isSolid()) {
                    startCd(player);
                    cancel();
                    return;
                }

                // Damage
                Collect.nearbyEntities(nextLocation, collectDistance).forEach(entity -> {
                    if (player.isSelfOrTeammateOrHasEffectResistance(entity)) {
                        return;
                    }

                    entity.damage(damage, player, DamageCause.SOTS);
                });

                // Fx
                player.spawnWorldParticle(nextLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);

                if (tick % 5 == 0) {
                    PlayerLib.playSound(nextLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                }
            }
        }.runTaskTimer(0, 1);

        startCdIndefinitely(player);

        return Response.AWAIT;
    }

}
