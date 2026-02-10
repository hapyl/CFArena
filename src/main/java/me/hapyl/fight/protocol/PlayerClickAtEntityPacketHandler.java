package me.hapyl.fight.protocol;

import me.hapyl.eterna.module.event.protocol.PacketReceiveEvent;
import me.hapyl.eterna.module.reflect.packet.wrapped.PacketWrappers;
import me.hapyl.eterna.module.reflect.packet.wrapped.WrappedServerboundInteractPacket;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;

public class PlayerClickAtEntityPacketHandler implements Listener {
    
    @EventHandler()
    public void handlePacketReceiveEvent(PacketReceiveEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final WrappedServerboundInteractPacket packet = ev.getWrappedPacket(PacketWrappers.SERVERBOUND_INTERACT);
        
        if (player == null || packet == null) {
            return;
        }
        
        final WrappedServerboundInteractPacket.WrappedAction action = packet.getAction();
        final WrappedServerboundInteractPacket.WrappedHand hand = action.getHand();
        
        if (hand == WrappedServerboundInteractPacket.WrappedHand.OFF_HAND) {
            return;
        }
        
        final WrappedServerboundInteractPacket.WrappedActionType type = action.getType();
        
        if (type != WrappedServerboundInteractPacket.WrappedActionType.ATTACK
            && type != WrappedServerboundInteractPacket.WrappedActionType.INTERACT_AT) {
            return;
        }
        
        final boolean isLeftClick = type == WrappedServerboundInteractPacket.WrappedActionType.ATTACK;
        
        CF.synchronizeToMainThread(() -> {
            final Entity entity = getEntityById(packet.getEntityId());
            
            if (entity == null) {
                return;
            }
            
            if (new PlayerClickAtEntityEvent(player, entity, isLeftClick).callEvent()) {
                return;
            }
            
            ev.setCancelled(true);
        });
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
