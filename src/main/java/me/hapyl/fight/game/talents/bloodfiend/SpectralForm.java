package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.bloodfield.BatCloud;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class SpectralForm extends Talent {

    @DisplayField public final double maxFlightHeight = 6;
    @DisplayField(scaleFactor = 500) public final float flightSpeed = 0.08f;

    public SpectralForm(@Nonnull Key key) {
        super(key, "Spectral Form");

        setDescription("""
                Call upon a swarm of bats and ride them, allowing to move swiftly for a short duration.
                
                You &ccannot&7 transfer vertically.
                You &acan&7 use talents, deal and take damage.
                """
        );

        setItem(Material.BLACK_DYE);
        setType(TalentType.MOVEMENT);

        setDurationSec(3);
        setCooldownSec(12);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final BatCloud batCloud = new BatCloud(player.getPlayer());

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(flightSpeed);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                final int distanceToGround = getDistanceToGround(player);

                if (distanceToGround >= maxFlightHeight) {
                    player.sendMessage("&6&l\uD83D\uDD4A &eThe bats are afraid of height!");
                    stopFlying();
                    return;
                }

                if (tick >= getDuration()) {
                    stopFlying();
                    return;
                }

                player.sendSubtitle("&2\uD83D\uDD4A &l" + CFUtils.formatTick(getDuration() - tick), 0, 5, 0);
                batCloud.tick();
            }

            private void stopFlying() {
                player.setAllowFlight(false);
                player.setFlying(false);

                player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 100);

                // Fx
                final Location location = player.getLocation();

                player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.0f);
                player.playWorldSound(location, Sound.ENTITY_BAT_DEATH, 0.0f);

                batCloud.remove();
                this.cancel();
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_BAT_TAKEOFF, 1.75f);

        return Response.OK;
    }

    private int getDistanceToGround(GamePlayer player) {
        final Location location = player.getLocation();

        int distance = 0;
        Block block = location.getBlock();

        while (!block.getType().isSolid() && !(distance > maxFlightHeight)) {
            block = block.getRelative(BlockFace.DOWN);
            distance++;
        }

        return distance;
    }
}
