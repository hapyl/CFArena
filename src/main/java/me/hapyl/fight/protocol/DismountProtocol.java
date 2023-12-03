package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import me.hapyl.fight.CF;
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
        final Player player = ev.getPlayer();
        final BoosterController boosters = Main.getPlugin().getBoosters();
        final PlayerMount mount = PlayerMount.getMount(player);

        // FIXME (hapyl): 003, Sep 3: yeah this looks like garbage but it's 5 AM
        CF.getPlayerOptional(player).ifPresent(gamePlayer -> {
            if (gamePlayer.blockDismount) {
                ev.setCancelled(true);
            }
        });

        if (mount != null) {
            ev.setCancelled(true);
        }

        if (boosters.isOnBooster(player)) {
            ev.setCancelled(true);
        }
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
    }

}
