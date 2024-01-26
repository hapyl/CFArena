package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.temporary.TemporaryPlayer;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.entity.Player;

public class ArcaneMuteProtocol extends ProtocolListener {
    public ArcaneMuteProtocol() {
        super(PacketType.Play.Server.NAMED_SOUND_EFFECT);
    }

    @Override
    public void onPacketReceiving(PacketEvent ev) {
    }

    @Override
    public void onPacketSending(PacketEvent ev) {
        final PacketContainer packet = ev.getPacket();
        final Player player = ev.getPlayer();

        if (player instanceof TemporaryPlayer) {
            return; // What? Why was this throwing a random error unrelated to named sound I'm so confused...
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer != null && gamePlayer.hasEffect(Effects.ARCANE_MUTE)) {
            ev.setCancelled(true);
        }
    }
}
