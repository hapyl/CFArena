package me.hapyl.fight.game.entity.packet;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.spigotutils.module.reflect.Reflect;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class EntityPacketFactory implements PacketFactory {

    private final GameEntity entity;
    private final int id;

    public EntityPacketFactory(GameEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
    }

    public final void sendPacket(@Nonnull Packet<?> packet) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (entity.is(player)) {
                return;
            }

            Reflect.sendPacket(player, packet);
        });
    }

    public final void sendPacketDelayed(@Nonnull Packet<?> packet, int delay) {
        GameTask.runLater(() -> sendPacket(packet), delay).setShutdownAction(ShutdownAction.IGNORE);
    }

    @Override
    @Nonnull
    public Packet<?> createRelMovePacket(short x, short y, short z, float yaw, float pitch) {
        return new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                id,
                x,
                y,
                z,
                (byte) (yaw * 256 / 360),
                (byte) (pitch * 256 / 360),
                true
        );
    }

    @Nonnull
    public Packet<?> createTeleportPacket() {
        return new PacketPlayOutEntityTeleport(entity.getNMSEntity());
    }

}
