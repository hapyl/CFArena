package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;

public class DismountProtocol extends ProtocolListener {

    public DismountProtocol() {
        super(PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final BoosterController boosters = Main.getPlugin().getBoosters();
        final PlayerMount mount = PlayerMount.getMount(player);

        if (player.blockDismount || mount != null || boosters.isOnBooster(player)) {
            ev.setCancelled(true);
        }
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
    }

}
