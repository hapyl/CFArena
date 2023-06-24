package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CloudFeatures extends MapFeature implements Listener {

    public CloudFeatures() {
        super("Boosters", "Propel yourself to another island. Do not fall though, it would be hard to explain your death...");

        // Register Boosters
        new Booster(518, 62, 505, 2.5, 1.0, -0.5);
        new Booster(539, 67, 495, -2.25, 0.5, 0.75);
        new Booster(552, 64, 521, -0.75, 0.5, 3.0);
        new Booster(535, 60, 555, -2.0, 1.0, -2.5);
        new Booster(517, 60, 528, 1.5, 1.0, 2.5);
        new Booster(499, 61, 538, 0.5, 0.75, 2.2);
        new Booster(507, 64, 559, -0.25, 1.0, -2.5);
        new Booster(472, 59, 499, -2.0, 0.2, 2.5);
        new Booster(458, 55, 526, 3.0, 1.25, 0.0);
        new Booster(479, 60, 475, -1.25, 1.0, -1.25);
        new Booster(460, 64, 461, -1.25, 0.25, 2.5);
        new Booster(453, 57, 486, 1.8, 0.75, 0.5);
        new Booster(496, 60, 462, 0.5, 2.25, -1.75);
        new Booster(499, 73, 448, -0.5, 0.0, 2.0);
        new Booster(538, 73, 446, 1.0, 1.0, 3.0);
        new Booster(528, 55, 460, -2.5, 1.0, 1.5);
    }

    @EventHandler()
    public void handlePlayerMove(PlayerMoveEvent ev) {
        if (!validateGameAndMap(GameMaps.CLOUDS)) {
            return;
        }

        final Player player = ev.getPlayer();
        final Location location = player.getLocation();

        if (location.getY() < 0) {
            final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
            if (gamePlayer.isAlive()) {
                gamePlayer.die(true);
                Achievements.BEYOND_CLOUDS.complete(player);
            }
        }
    }

    @Override
    public void tick(int tick) {

    }
}
