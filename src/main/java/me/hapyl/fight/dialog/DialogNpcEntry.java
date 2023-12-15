package me.hapyl.fight.dialog;

import me.hapyl.fight.npc.PersistentNPC;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
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

        Chat.sendMessage(player, "&e[NPC] %s&f: %s", npc.getName(), Placeholder.formatAll(string, player, npc));
        PlayerLib.playSound(player, npc.sound.getSound(), npc.sound.getPitch());
    }
}
