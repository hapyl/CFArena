package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.MetadataEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.gui.styled.eye.EyeGUI;
import me.hapyl.fight.npc.TheEyeNPC;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ViewTheEyeGuiCommand extends CFCommand {
    public ViewTheEyeGuiCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        if (!MetadataEntry.has(player, TheEyeNPC.HAS_UNLOCKED_REMOTE_GUI)) {
            Message.error(player, "You haven't unlocked this feature yet!");
            Message.sound(player, SoundEffect.ERROR);
            return;
        }

        new EyeGUI(player);
    }
}
