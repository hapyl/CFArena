package me.hapyl.fight.notifier;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import org.bukkit.entity.Player;

public class LinkNotify extends StringNotify {

    private final String link;

    public LinkNotify(String string, String link) {
        super(string);
        this.link = link;
    }

    @Override
    public void sendString(Player player) {
        final String string = centerString();

        Chat.sendClickableHoverableMessage(player, LazyEvent.openUrl(link), LazyEvent.showText("&eClick to open the link!"), string);
    }
}
