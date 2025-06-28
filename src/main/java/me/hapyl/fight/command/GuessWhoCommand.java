package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.activity.ActivityHandler;
import me.hapyl.fight.game.cosmetic.gadget.guesswho.GuessWhoActivity;
import me.hapyl.fight.game.cosmetic.gadget.guesswho.GuessWhoLobbyGUI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GuessWhoCommand extends SimplePlayerCommand {
    
    public GuessWhoCommand(@Nonnull String name) {
        super(name);
        
        setAliases("gw", "who");
    }
    
    @Override
    protected void execute(Player player, String[] args) {
        final GuessWhoActivity currentGame = ActivityHandler.getActivity(player, GuessWhoActivity.class);
        
        if (currentGame != null) {
            currentGame.getPlayer(player).promptGUI();
        }
        else {
            new GuessWhoLobbyGUI(player);
        }
    }
}
