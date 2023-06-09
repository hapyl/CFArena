package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.hapyl.fight.game.Debug;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;

public class HandshakeProtocol extends ProtocolListener {
    public HandshakeProtocol() {
        super(PacketType.Handshake.Client.SET_PROTOCOL);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final PacketContainer packet = event.getPacket();
        final StructureModifier<InternalStructure> modifiers = packet.getStructures();

        Debug.info(modifiers.toString());
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
