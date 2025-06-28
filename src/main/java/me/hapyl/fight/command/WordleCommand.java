package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.gadget.wordle.WordleTypeGUI;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WordleCommand extends CFCommand {
    public WordleCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);
    }
    
    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        if (!Registries.cosmetics().WORDLE.isUnlocked(player)) {
            Message.error(player, "You haven't unlocked this yet!");
            return;
        }
        
        new WordleTypeGUI(player);
    }
}
