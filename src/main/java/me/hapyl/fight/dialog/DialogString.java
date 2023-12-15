package me.hapyl.fight.dialog;

import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class DialogString implements DialogEntry {

    protected final String string;

    public DialogString(String string) {
        this.string = string;
    }

    @Override
    public void display(@Nonnull ActiveDialog dialog) {
        dialog.getPlayer().sendMessage(ChatColor.ITALIC + string);
    }

    @Override
    public int getDelay() {
        return Numbers.clamp(string.length(), 20, 200);
    }
}
