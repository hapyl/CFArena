package me.hapyl.fight.protocol;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.features.BoosterController;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class DismountPacketHandler implements Listener {

    @EventHandler
    public void handle(EntityDismountEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final BoosterController boosters = CF.getPlugin().getBoosters();
        final PlayerMount mount = PlayerMount.getMount(gamePlayer);

        if (gamePlayer.blockDismount || mount != null || boosters.isOnBooster(gamePlayer)) {
            ev.setCancelled(true);
        }
    }

}
