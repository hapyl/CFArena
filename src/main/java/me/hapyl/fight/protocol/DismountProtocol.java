package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.entity.Player;

public class DismountProtocol extends ProtocolListener {
    public DismountProtocol() {
        super(PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent ev) {
        final BoosterController boosters = Main.getPlugin().getBoosters();
        final Player player = ev.getPlayer();

        if (boosters.isOnBooster(player)) {
            ev.setCancelled(true);
        }
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
    }

}
