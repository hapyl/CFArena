package me.hapyl.fight.protocol;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import me.hapyl.eterna.module.reflect.packet.wrapped.PacketWrappers;
import me.hapyl.eterna.module.reflect.packet.wrapped.WrappedPacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;

public class PlayerClickAtEntityProtocol implements Listener {

    @EventHandler()
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final WrappedPacketPlayInUseEntity packet = ev.getWrappedPacket(PacketWrappers.PACKET_PLAY_IN_USE_ENTITY);

        if (player == null || packet == null) {
            return;
        }

        final WrappedPacketPlayInUseEntity.WrappedAction action = packet.getAction();
        final WrappedPacketPlayInUseEntity.WrappedHand hand = action.getHand();

        if (hand == WrappedPacketPlayInUseEntity.WrappedHand.OFF_HAND) {
            return;
        }

        final WrappedPacketPlayInUseEntity.WrappedActionType type = action.getType();

        if (type != WrappedPacketPlayInUseEntity.WrappedActionType.ATTACK
                && type != WrappedPacketPlayInUseEntity.WrappedActionType.INTERACT_AT) {
            return;
        }

        final boolean isLeftClick = type == WrappedPacketPlayInUseEntity.WrappedActionType.ATTACK;

        new BukkitRunnable() {
            @Override
            public void run() {
                final Entity entity = getEntityById(packet.getEntityId());

                if (entity == null) {
                    return;
                }

                if (new PlayerClickAtEntityEvent(player, entity, isLeftClick).callAndCheck()) {
                    return;
                }

                ev.setCancelled(true);
            }
        }.runTask(Main.getPlugin());
    }

    @Nullable
    private Entity getEntityById(int id) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) {
                    return entity;
                }
            }
        }

        return null;
    }

}
