package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
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

        if (GamePlayer.getPlayer(player).hasEffect(GameEffectType.ARCANE_MUTE)) {
            ev.setCancelled(true);
        }
    }
}
