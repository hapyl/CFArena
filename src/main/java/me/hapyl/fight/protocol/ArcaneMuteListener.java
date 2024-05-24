package me.hapyl.fight.protocol;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.event.protocol.PacketSendEvent;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArcaneMuteListener implements Listener {

    @EventHandler()
    public void handlePacketSendEvent(PacketSendEvent ev) {
        final Player player = ev.getPlayer();
        final PacketPlayOutNamedSoundEffect packet = ev.getPacket(PacketPlayOutNamedSoundEffect.class);

        if (packet == null) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer != null && gamePlayer.hasEffect(Effects.ARCANE_MUTE)) {
            ev.setCancelled(true);
        }
    }
}
