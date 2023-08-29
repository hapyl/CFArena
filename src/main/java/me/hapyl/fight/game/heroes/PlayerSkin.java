package me.hapyl.fight.game.heroes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.reflect.Reflect;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerSkin {

    private final String texture;
    private final String signature;

    public PlayerSkin(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    public void apply(Player player) {
        final EntityPlayer nmsPlayer = Reflect.getMinecraftPlayer(player);
        final GameProfile gameProfile = nmsPlayer.fM();
        final PropertyMap properties = gameProfile.getProperties();

        removePlayer(player);

        properties.removeAll("textures");
        properties.put("textures", new Property("textures", texture, signature));

        createPlayer(player);
    }

    private void removePlayer(Player player) {
        final ClientboundPlayerInfoRemovePacket remove = new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId()));

        sendPacket(remove, null);
    }

    private void createPlayer(Player player) {
        final ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.a.a,
                Reflect.getMinecraftPlayer(player)
        );

        GameTask.runLater(() -> {
            sendPacket(packet, null);
        }, 60);
    }

    private void sendPacket(Packet<?> packet, @Nullable Player ignore) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (ignore != null && ignore == player) {
                return;
            }

            Reflect.sendPacket(player, packet);
        });
    }

}
