package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class NoProfileException extends RuntimeException {

    public NoProfileException() {
        super();
    }

    public NoProfileException(Player player) {
        this(player, "");
    }

    public NoProfileException(Player player, String string) {
        super(string);

        Chat.sendMessage(player, "&4Error! &cCould not fetch your profile! " + string);
    }

}
