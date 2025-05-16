package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
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
        if (args.length == 0) {
            new HeroSelectGUI(player);
            return;
        }
        
        final Hero hero = HeroRegistry.ofStringOrNull(args[0]);
        
        if (hero == null) {
            Message.error(player, "No such hero {%s}!".formatted(args[0]));
            return;
        }
        
        CF.getPlugin()
          .getManager()
          .setSelectedHero(player, hero, args.length >= 2 && args[1].equals(Hero.DISABLED_HERO_FLAG));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.completerSort(HeroRegistry.keys(), args);
    }
    
}