package me.hapyl.fight.event;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import javax.annotation.Nonnull;

// handles server-related events
public class ServerHandler implements Listener {

    @EventHandler()
    public void handleServerList(ServerListPingEvent ev) {
        ev.setMaxPlayers(0);
        ev.setMotd(centerMotd(
                Main.GAME_NAME,
                "&3Hosted by &bServer.pro"
        ));
    }

    public String centerMotd(@Nonnull String header, @Nonnull String footer) {
        return centerText(header, Target.MOTD) + "\n" + centerText(footer, Target.MOTD);
    }

    /**
     * <a href="https://www.spigotmc.org/threads/center-motds-and-messages.354209/">Author.</a>
     */
    public String centerText(String text, Target target) {
        char[] chars = text.toCharArray();
        boolean isBold = false;
        double length = 0;
        ChatColor pholder = null;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && chars.length != (i + 1) && (pholder = ChatColor.getByChar(chars[i + 1])) != null) {
                if (pholder != ChatColor.UNDERLINE && pholder != ChatColor.ITALIC
                        && pholder != ChatColor.STRIKETHROUGH && pholder != ChatColor.MAGIC) {
                    isBold = (chars[i + 1] == 'l');
                    length--;
                    i += isBold ? 1 : 0;
                }
            }
            else {
                length++;
                length += (isBold ? (chars[i] != ' ' ? 0.1555555555555556 : 0) : 0);
            }
        }

        double spaces = (target.length - length) / 2;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(' ');
        }

        String copy = builder.toString();
        builder.append(text).append(copy);

        return Chat.format(builder.toString());
    }

    enum Target {
        CHAT(80),
        MOTD(45);

        private final int length;

        Target(int length) {
            this.length = length;
        }
    }

}
