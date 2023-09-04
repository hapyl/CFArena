package me.hapyl.fight.game.heroes.archive.bloodfield;

import me.hapyl.fight.game.entity.Ticking;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class BatCloud implements Ticking {

    private static final double OFFSET = 0.35d;
    private static final double Y_OFFSET = 0.15d;

    private static final double[][] BAT_OFFSET = {
            { 0.0d, 0.0d, OFFSET, 0.0f },
            { OFFSET, 0.0d, 0.0d, -90.0f },
            { -OFFSET, 0.0d, 0.0d, 90.0f },
            { 0.0d, 0.0d, -OFFSET, -180.0f },
            { OFFSET, Y_OFFSET, OFFSET, -45.0f },
            { -OFFSET, Y_OFFSET, OFFSET, 45.0f },
            { OFFSET, Y_OFFSET, -OFFSET, -135.0f },
            { -OFFSET, Y_OFFSET, -OFFSET, 135.0f }
    };

    private final Player player;
    private final Bat[] bats;

    public BatCloud(Player player) {
        this.player = player;
        this.bats = new Bat[8];

        iterateLocation((i, location) -> {
            bats[i] = spawnBat(location);
        });
    }

    @Override
    public void tick() {
        iterateLocation((i, location) -> {
            bats[i].teleport(location);
        });

        // Fx
        final Location playerLocation = player.getLocation();

        PlayerLib.spawnParticle(playerLocation, Particle.ASH, 10, 0.25d, 0.1d, 0.25d, 0.0f);
    }

    public void remove() {
        for (Bat bat : bats) {
            bat.setAI(true);
        }

        GameTask.runLater(() -> {
            for (Bat bat : bats) {
                final Location location = bat.getLocation();
                bat.remove();

                // Fx
                PlayerLib.spawnParticle(location, Particle.SMOKE_NORMAL, 3, 0.1d, 0.1d, 0.1d, 0.025f);
            }

            PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 1.25f);
        }, 20);

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.0f);
    }

    private void iterateLocation(BiConsumer<Integer, Location> consumer) {
        final Location location = player.getLocation().subtract(0.0d, 0.3d, 0.0d);

        for (int i = 0; i < BAT_OFFSET.length; i++) {
            final double[] offset = BAT_OFFSET[i];
            final double x = offset[0];
            final double y = offset[1];
            final double z = offset[2];
            final double yaw = offset[3];

            location.add(x, y, z);
            location.setYaw((float) yaw);

            consumer.accept(i, location);

            location.subtract(x, y, z);
        }
    }

    private Bat spawnBat(Location location) {
        return Entities.BAT.spawn(location, self -> {
            self.setAI(false);
            self.setAwake(true);
            self.setInvulnerable(true);
            self.setSilent(true);

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY, player);
        });
    }
}
