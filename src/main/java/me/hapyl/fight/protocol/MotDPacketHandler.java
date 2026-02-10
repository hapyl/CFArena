package me.hapyl.fight.protocol;

import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MotDPacketHandler implements Listener {

    /**
     * """
     * &f&l  &6█▀▀ █▀▀ ▄▀█ █▀█ █▀▀ █▄ █ ▄▀█
     * &f&l  &e█▄▄ █▀   █▀█ █▀▄ ██▄ █ ▀█ █▀█     &8%s
     * """.formatted(CF.getVersionNoSnapshot()
     */

    private final CachedServerIcon cachedServerIcon;
    private final String motD;

    public MotDPacketHandler() {
        cachedServerIcon = loadFavicon();
        motD = createMotD();
    }

    @EventHandler()
    public void handleServerListPingEvent(ServerListPingEvent ev) {
        ev.setServerIcon(cachedServerIcon);
        ev.setMotd(createMotD());

        ev.setMaxPlayers(CF.getOnlinePlayerCount() + 1);
    }

    private String createMotD() {
        //˚　　　　✦　　　.　　. 　˚　.　　　　　 . ✦　　　 　˚　　　　 . ★⋆. ࿐࿔
        //　　　. 　　˚　　 　　*　　 　　✦　　　.　　.　　　✦　˚ 　　　　˚　.˚　　　　✦　　　.　　. 　˚　.　　　　 　　 　　　　        ੈ✧̣̇˳·˖✶   ✦　　

        return Chat.format("""
                &7˚　　  &f✦&7　&7.　&8. 　&7˚　&7.　　 &7.   &f✮   &8⌈ &b&lᴄғ &bᴀʀᴇɴᴀ &8⌋   &8⌈ &8v%s &8⌋
                &7　&7. 　&8˚　　　　&7* 　　&7✦　　&8. %s
                """.formatted(
                CF.getVersionNoSnapshot(),
                CenterChat.makeString("&8⌈ %s &8⌋".formatted(Main.updateTopic.getTopic()), 70)
        ));

        // ˗ˏˋ | ˎˊ˗
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

    private static String makeMotD(String string) {
        return Chat.color(CenterChat.makeString(string, 122));
    }

}
