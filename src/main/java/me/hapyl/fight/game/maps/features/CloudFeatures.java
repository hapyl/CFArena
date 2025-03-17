package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CloudFeatures extends LevelFeature implements Listener {

    public CloudFeatures() {
        super("Boosters", """
                Propel yourself to another island. Do not fall though, it would be hard to explain your death...
                """);

        // Register Boosters
        new Booster(3518, 62, 5, 2.5, 1.0, -0.5);
        new Booster(3539, 67, -5, -2.25, 0.5, 0.75);
        new Booster(3552, 64, 21, -0.75, 0.5, 3.0);
        new Booster(3535, 60, 55, -2.0, 1.0, -2.5);
        new Booster(3517, 60, 28, 1.5, 1.0, 2.5);
        new Booster(3499, 61, 38, 0.5, 0.75, 2.2);
        new Booster(3507, 64, 59, -0.25, 1.0, -2.5);
        new Booster(3472, 59, -1, -2.0, 0.2, 2.5);
        new Booster(3458, 55, 26, 3.0, 1.25, 0.0);
        new Booster(3479, 60, -25, -1.25, 1.0, -1.25);
        new Booster(3460, 64, -39, -1.25, 0.25, 2.5);
        new Booster(3453, 57, -14, 1.8, 0.75, 0.5);
        new Booster(3496, 60, -38, 0.5, 2.25, -1.75);
        new Booster(3499, 73, -52, -0.5, 0.0, 2.0);
        new Booster(3538, 73, -54, 1.0, 1.0, 3.0);
        new Booster(3528, 55, -40, -2.5, 1.0, 1.5);
        new Booster(3436, 56, -32, 1.25, 0.5, 1.5);
    }

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        if (!validateGameAndMap(EnumLevel.CLOUDS)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final Location location = player.getLocation();

        if (location.getY() < 0) {
            if (player.isAlive()) {
                player.die(true);
                Registries.achievements().BEYOND_CLOUDS.complete(player);
            }
        }
    }

}
