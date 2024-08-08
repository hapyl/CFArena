package me.hapyl.fight.protocol;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.UpdateTopic;
import me.hapyl.fight.VersionInfo;
import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MotDProtocol implements Listener {

    private final CachedServerIcon cachedServerIcon;
    private final String motD;

    public MotDProtocol() {
        cachedServerIcon = loadFavicon();
        motD = createMotD();
    }

    @EventHandler()
    public void handleServerListPingEvent(ServerListPingEvent ev) {
        ev.setServerIcon(cachedServerIcon);
        ev.setMotd(motD);

        ev.setMaxPlayers(CF.getOnlinePlayerCount() + 1);
    }

    private String createMotD() {
        final VersionInfo versionInfo = Main.versionInfo;
        final StringBuilder builder = new StringBuilder();

        int index = 0;
        for (UpdateTopic topic : versionInfo.getUpdateTopic()) {
            if (index++ != 0) {
                builder.append(" &7◆ ");
            }

            builder.append(topic.getTopic());
        }

        return makeMotD("&6&lᴄғ ᴀʀᴇɴᴀ &7◆ &8v%s".formatted(CF.getVersionNoSnapshot()))
                + "\n"
                + makeMotD(builder.toString());
    }

    private String makeMotD(String string) {
        return Chat.color(CenterChat.makeString(string, CenterChat.CENTER_PX_MOTD));
    }

    private CachedServerIcon loadFavicon() {
        try {
            final Main plugin = Main.getPlugin();
            final InputStream resource = plugin.getResource("favicon.png");

            if (resource == null) {
                return null;
            }

            final BufferedImage image = ImageIO.read(resource);

            final int width = image.getWidth();
            final int height = image.getHeight();

            if (width != 64 || height != 64) {
                throw new IllegalArgumentException("Favicon must be 64x64, not %sx%s!".formatted(width, height));
            }

            return Bukkit.loadServerIcon(image);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error loading favicon! " + e.getMessage());
        }
    }

}
