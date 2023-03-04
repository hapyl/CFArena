package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.database.Database;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CosmeticsListener implements Listener {

    @EventHandler()
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        // Check if player move a full block
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        // Check if player has a contrail
        final Cosmetics selectedContrail = Database.getDatabase(player).getCosmetics().getSelected(Type.CONTRAIL);
        if (selectedContrail == null) {
            return;
        }

        selectedContrail.getCosmetic().onDisplay(player);
    }

}
