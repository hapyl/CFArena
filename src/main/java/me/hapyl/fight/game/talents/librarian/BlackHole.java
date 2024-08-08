package me.hapyl.fight.game.talents.librarian;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.librarian.Librarian;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class BlackHole extends LibrarianTalent {

    // FIXME: 027, Mar 27, 2023 -> this works without grimoire

    public BlackHole() {
        super("Black Hole");

        addDescription(
                "Creates a black hole at your target block. Pulling enemies in and dealing <scaled> damage per b based on &cGrimoire &7level."
        );

        setItem(Material.BLACK_CANDLE);
    }

    @Override
    public Response executeGrimoire(@Nonnull GamePlayer player) {
        final Block block = player.getTargetBlockExact(10);

        if (block == null) {
            return Response.error("&cNo valid target block!");
        }

        final Location location = block.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
        PlayerLib.spawnParticle(location, Particle.GLOW, 1, 0, 0, 0, 0);

        final double suckRadius = 3.0d;
        GameTask.runTaskTimerTimes((task, tick) -> {

            // FX
            final double tick60 = tick / 60d;
            final double tick16 = tick / 16d;
            final double tick120 = tick / 120d;

            for (double i = 0; i < Math.PI * 2; i += (Math.PI / 4)) {
                final double x = (tick16 * Math.sin(i + tick60));
                final double z = (tick16 * Math.cos(i + tick60));
                location.add(x, tick120, z);
                PlayerLib.spawnParticle(location, Particle.WITCH, 1, 0, 0, 0, 0);
                location.subtract(x, tick120, z);
            }

            Collect.nearbyEntities(location, suckRadius).forEach(entity -> {
                if (entity.equals(player)) {
                    return;
                }

                final Location entityLocation = entity.getLocation();
                entity.setVelocity(location.toVector().subtract(entityLocation.toVector()).multiply(0.2d));

                if (tick % 20 == 0) {
                    entity.damage(getCurrentValue(Heroes.LIBRARIAN.getHero(Librarian.class).getGrimoireLevel(player)));
                    entity.spawnWorldParticle(entityLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                    entity.playSound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
                }

            });

            PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, (float) Math.min(1.2f + (tick120), 2.0f));

        }, 1, 60);

        return Response.OK;
    }

    @Override
    public int getGrimoireCd() {
        return 60;
    }

    @Override
    public double[] getValues() {
        return new double[] { 5.0d, 7.5d, 10.0d, 12.5d };
    }
}
