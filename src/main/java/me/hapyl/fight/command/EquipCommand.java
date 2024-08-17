package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class EquipCommand extends SimplePlayerAdminCommand {
    public EquipCommand(String name) {
        super(name);

        setUsage("/equip <hero>");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Hero hero = HeroRegistry.ofStringOrNull(args[0]);

        if (hero == null) {
            Chat.sendMessage(player, "&cInvalid hero!");
            return;
        }

        hero.getEquipment().equip(player);
        PlayerLib.playSound(player, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
        Chat.sendMessage(player, "&aEquipped &e%s&a!".formatted(hero.getName()));
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return completerSort(HeroRegistry.playable(), args);
    }
}
