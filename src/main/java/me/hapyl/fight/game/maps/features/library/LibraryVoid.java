package me.hapyl.fight.game.maps.features.library;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.GameEntityContactPortalEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.features.VoidFeature;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LibraryVoid extends VoidFeature implements Listener {

    private final LibraryKeyport portals;

    public LibraryVoid() {
        super(
                "The Void",
                "A chunk of void that can transport you anywhere. But be aware that continuous usage may as well consume you..."
        );

        portals = new LibraryKeyport();
    }

    @EventHandler()
    public void handleMovement(PlayerMoveEvent ev) {
        if (!validateGameAndMap(GameMaps.LIBRARY)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        if (!portals.testPlayer(player)) {
            return;
        }

        addVoidValue(player, 1);
    }

    @EventHandler()
    public void handlePortalEvent(GameEntityContactPortalEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (!validateCurrentMap(GameMaps.LIBRARY)) {
            return;
        }

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        final BlockLocation exit = portals.getRandom();

        player.teleport(exit.toLocation(player.getLocation()));
        addVoidValue(player, 2);
    }

    @Override
    public void onStart() {
        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                portals.getEntrances().forEach(blockLoc -> {
                    final Location location = blockLoc.toLocation().add(0.0d, 1.5d, 0.0d);

                    PlayerLib.spawnParticle(location, Particle.PORTAL, 10, 0.2d, 1.5d, 0.2d, 1.0f);
                    PlayerLib.spawnParticle(location, Particle.REVERSE_PORTAL, 10, 0.2d, 1.5d, 0.2d, 0.5f);
                    PlayerLib.spawnParticle(location, Particle.ENCHANT, 10, 0.2d, 1.5d, 0.2d, 1.0f);
                });

                if (tick % 200 == 0) {
                    CF.getAlivePlayers().forEach(player -> removeVoidValue(player));
                }

                tick += 5;
            }
        }.runTaskTimer(5, 5);
    }

}
