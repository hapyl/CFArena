package me.hapyl.fight.game.playerskin;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerSkinHandler implements Listener {



    @EventHandler()
    public void handleTeleport(PlayerTeleportEvent ev) {
        final Location to = ev.getTo();
    }

}
