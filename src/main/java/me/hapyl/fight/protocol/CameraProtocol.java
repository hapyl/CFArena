package me.hapyl.fight.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.spigotutils.module.reflect.protocol.ProtocolListener;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;

public class CameraProtocol extends ProtocolListener {

    private final Map<Player, Integer> lastCameraId;

    public CameraProtocol() {
        super(PacketType.Play.Server.CAMERA);

        this.lastCameraId = Maps.newHashMap();
    }

    @Override
    public void onPacketReceiving(PacketEvent ev) {
    }

    @Override
    public void onPacketSending(PacketEvent ev) {
        final Player player = ev.getPlayer();
        final int playerId = player.getEntityId();
        final int cameraId = ev.getPacket().getIntegers().read(0);

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
