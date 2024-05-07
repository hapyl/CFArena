package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.event.custom.GameEntityContactPortalEvent;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LimboFeature extends VoidFeature implements Listener {
    public LimboFeature() {
        super("The Sea of Nothingness", """
                A very mysterious sea surrounds this island.
                                
                If you decide to swim in it, prepare for pain.
                """);
    }

    @EventHandler()
    public void handlePortalEvent(GameEntityContactPortalEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final GameMap currentMap = Manager.current().getCurrentMap().getMap();

        if (!validateCurrentMap(GameMaps.LIMBO)) {
            return;
        }

        entity.damage(20);
        Location teleportLocation = currentMap.getLocation();

        // Handle player differently
        if (entity instanceof GamePlayer player) {
            addVoidValue(player, 2);
        }
        else {
            final GameTeam team = entity.getTeam();

            if (team != null) {
                for (GamePlayer player : team.getPlayers()) {
                    if (player.isDeadOrRespawning()) {
                        continue;
                    }

                    teleportLocation = player.getLocation();
                }
            }
        }

        // Should not happen, just in case
        if (entity.getWorld() != teleportLocation.getWorld()) {
            return;
        }

        final Location location = entity.getLocation();
        entity.teleport(teleportLocation);

        // Fx
        entity.addEffect(Effects.DARKNESS, 255, 40);
        entity.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);

        Geometry.drawLine(location, teleportLocation, 0.5,
                loc -> PlayerLib.spawnParticle(loc, Particle.SMOKE, 3, 0.1d, 0.1d, 0.1d, 0.01f)
        );
    }
}
