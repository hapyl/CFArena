package me.hapyl.fight.dialog;

import org.bukkit.entity.Player;

public class DialogString implements DialogEntry {

    private final String string;

    public DialogString(String string) {
        this.string = string;
    }

    @Override
    public void display(Player player) {

    }
}
