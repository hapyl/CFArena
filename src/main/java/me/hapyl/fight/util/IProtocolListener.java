package me.hapyl.fight.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import javax.annotation.Nonnull;

/**
 * An interface version of {@link me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener}.
 * <p>
 * Must be registered via {@link me.hapyl.fight.CF#registerProtocolListener(IProtocolListener)}.
 */
public interface IProtocolListener {

    /**
     * Gets the {@link PacketType} this listener listens to.
     *
     * @return the packet type.
     */
    @Nonnull
    PacketType getPacketType();

    /**
     * Called upon receiving a packet from a client.
     *
     * @param ev - Packet event.
     */
    void onPacketReceiving(@Nonnull PacketEvent ev);

    /**
     * Called upon sending a packet to a client.
     *
     * @param ev - Packet event.
     */
    void onPacketSending(@Nonnull PacketEvent ev);

}
