package me.hapyl.fight.util;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.List;

public class ChatUtils {

    @Nonnull
    public static HoverEvent showText(@Nonnull String... strings) {
        final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text(strings[0]));
        event.addContent(nl());

        for (int i = 1; i < strings.length; i++) {
            if (i != 1) {
                event.addContent(nl());
            }

            final String string = strings[i];
            final List<String> list = ItemBuilder.splitString(string);

            for (int j = 0; j < list.size(); j++) {
                if (j != 0) {
                    event.addContent(nl());
                }

                event.addContent(text(list.get(j)));
            }
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
