package me.hapyl.fight.notifier;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public interface Notify {

    static Notify string(String string) {
        return new StringNotify(string);
    }

    static LinkNotify link(String string, String link) {
        return new LinkNotify(string, link);
    }

    void sendString(Player player);

    static List<String> splitString(String string) {
        return ItemBuilder.splitString(string, 154);
    }

}
