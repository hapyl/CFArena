package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.event.custom.PlayerClickAtEntityEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;

public class PlayerClickAtEntityProtocol extends ProtocolListener {
    public PlayerClickAtEntityProtocol() {
        super(PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final PacketContainer packet = event.getPacket();
        final Integer entityId = packet.getIntegers().read(0);
        final WrappedEnumEntityUseAction useAction = packet.getEnumEntityUseActions().read(0);
        final EnumWrappers.EntityUseAction action = useAction.getAction();

        if (action != EnumWrappers.EntityUseAction.ATTACK && action != EnumWrappers.EntityUseAction.INTERACT_AT) {
            return;
        }

        final boolean isLeftClick = action == EnumWrappers.EntityUseAction.ATTACK;

        new BukkitRunnable() {
            @Override
            public void run() {
                final Entity entity = getEntityById(entityId);

                if (entity == null) {
                    return;
                }

                if (new PlayerClickAtEntityEvent(gamePlayer, entity, isLeftClick).callAndCheck()) {
                    return;
                }

                event.setCancelled(true);
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

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
