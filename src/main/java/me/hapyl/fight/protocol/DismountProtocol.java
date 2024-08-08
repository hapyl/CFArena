package me.hapyl.fight.protocol;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DismountProtocol implements Listener {

    @EventHandler()
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final PacketPlayInSteerVehicle packet = ev.getPacket(PacketPlayInSteerVehicle.class);

        if (player == null || packet == null) {
            return;
        }

        final BoosterController boosters = Main.getPlugin().getBoosters();
        final PlayerMount mount = PlayerMount.getMount(player);

        if (player.blockDismount || mount != null || boosters.isOnBooster(player)) {
            ev.setCancelled(true);
        }
    }


}
