package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.chat.Chat;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class ChatUtils {

    @Nonnull
    public static HoverEvent showText(@Nonnull String first, @Nonnull String... other) {
        final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text(first));

        for (String string : other) {
            event.addContent(nl());
            event.addContent(text(string));
        }

        return event;
    }

    private static Text nl() {
        return text("\n");
    }

    private static Text text(String text) {
        return new Text(ChatColor.GRAY + Chat.format(text));
    }

}
