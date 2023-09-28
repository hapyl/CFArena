package me.hapyl.fight.command;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.gui.HeroSelectGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.util.Validate;
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
            final Heroes hero = Validate.getEnumValue(Heroes.class, args[0]);

            if (hero == null) {
                Chat.sendMessage(player, "&cNo such hero as '%s'!", args[0]);
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
        return super.completerSort(super.arrayToList(Heroes.values()), args);
    }

}