package me.hapyl.fight.protocol;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.event.protocol.PacketSendEvent;
import me.hapyl.eterna.module.reflect.Reflect;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.Map;

public class CameraListener implements Listener {

    private final Map<Player, Integer> lastCameraId;

    public CameraListener() {
        this.lastCameraId = Maps.newHashMap();
    }

    @EventHandler()
    public void handlePacketSendEvent(PacketSendEvent ev) {
        final Player player = ev.getPlayer();
        final PacketPlayOutCamera packet = ev.getPacket(PacketPlayOutCamera.class);

        if (packet == null) {
            return;
        }

        final int playerId = player.getEntityId();
        final Integer cameraId = Reflect.getDeclaredFieldValue(packet, "a", Integer.class);

        if (cameraId == null) {
            return;
        }

        // different Id = enter
        // same Id      = leave
        if (playerId != cameraId) {
            lastCameraId.put(player, cameraId);
            return;
        }

        GamePlayer.getPlayerOptional(player).ifPresent(gamePlayer -> {
            if (gamePlayer.blockDismount) {
                final Entity entity = getEntityById(player.getWorld(), lastCameraId.get(player));

                if (entity != null && player.getGameMode() == GameMode.SPECTATOR) {
                    player.setSpectatorTarget(entity);
                }
            }
        });
    }

    @Nullable
    private Entity getEntityById(World world, int id) {
        for (Entity entity : world.getEntities()) {
            if (entity.getEntityId() == id) {
                return entity;
            }
        }

        return null;
    }

}
