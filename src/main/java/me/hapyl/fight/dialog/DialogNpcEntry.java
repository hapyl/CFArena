package me.hapyl.fight.dialog;

import me.hapyl.fight.npc.PersistentNPC;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DialogNpcEntry extends DialogString {

    private final PersistentNPC npc;

    public DialogNpcEntry(PersistentNPC npc, String string) {
        super(string);
        this.npc = npc;
    }

    @Override
    public void display(@Nonnull ActiveDialog dialog) {
        final Player player = dialog.getPlayer();

        npc.sendMessage(player, Placeholder.formatAll(string, player, npc));
    }
}
