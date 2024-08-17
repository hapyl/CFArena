package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.gui.HeroSelectGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HeroCommand extends SimplePlayerCommand {

    public HeroCommand(String str) {
        super(str);
        setUsage("hero (Hero)");
        setDescription("Allows selecting a hero to play as!");
        setAliases("class");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cUnable to change hero during the game!");
            return;
        }

        if (args.length >= 1) {
            final Hero hero = HeroRegistry.ofStringOrNull(args[0]);

            if (hero == null) {
                Chat.sendMessage(player, "&cNo such hero as '%s'!".formatted(args[0]));
                return;
            }

            Main.getPlugin()
                    .getManager()
                    .setSelectedHero(player, hero, args.length >= 2 && args[1].equals("-IKnowItsDisabledHeroAndWillBreakTheGame"));
            return;
        }

        new HeroSelectGUI(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.completerSort(HeroRegistry.keys(), args);
    }

}