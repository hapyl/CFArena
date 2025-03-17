package me.hapyl.fight.proxy;

import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class TransferManager implements Listener {

    public static final NamespacedKey FROM_SERVER = NamespacedKey.minecraft("from_server");

    public static void transfer(@Nonnull Player player, @Nonnull ServerType type) {
        final ServerType currentType = ServerType.currentType();

        if (currentType == type) {
            Message.error(player, "You are already on {%s} server!".formatted(currentType));
            return;
        }

        if (Manager.current().isGameInProgress()) {
            Message.error(player, "Cannot transfer while the game is in progress!");
            return;
        }

        Message.info(player, "Transferring you to {%s}...".formatted(type));

        // Check if the server is enabled
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!type.isEnabled()) {
                    Message.error(player, "Unable to transfer! {%s} is offline!".formatted(type));
                    return;
                }

                try {
                    player.storeCookie(FROM_SERVER, currentType.name().getBytes(StandardCharsets.UTF_8));
                    player.transfer(getHostAddress(), type.port);

                    accept(player, currentType, type);
                } catch (IllegalStateException illegalStateException) {
                    illegalStateException.printStackTrace();

                    Message.error(player, "Unable to transfer! {%s}".formatted(illegalStateException.getMessage()));
                }
            }
        }.runTaskAsynchronously(CF.getPlugin());
    }

    @Nonnull
    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static void accept(@Nonnull Player player, @Nonnull ServerType from, @Nonnull ServerType to) {
        Message.broadcastStaff("Transferred {%s} from {%s} ➠ {%s}.".formatted(player.getName(), from, to));
    }
}
