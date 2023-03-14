package me.hapyl.fight.notifier;

import me.hapyl.spigotutils.module.chat.CenterChat;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class StringNotify implements Notify {

    protected final String string;

    public StringNotify(String string) {
        this.string = string;
    }

    @Override
    public void sendString(Player player) {
        Chat.sendMessage(player, centerString());
    }

    public String centerString() {
        return CenterChat.makeString(string);
    }
}
