package me.hapyl.fight.game.entity.packet;

import net.minecraft.network.protocol.Packet;

import javax.annotation.Nonnull;

public interface PacketFactory {
    @Nonnull
    Packet<?> createRelMovePacket(short x, short y, short z, float yaw, float pitch);
}
