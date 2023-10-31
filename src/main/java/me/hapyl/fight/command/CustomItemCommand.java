package me.hapyl.fight.command;

import me.hapyl.fight.item.Items;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class CustomItemCommand extends SimplePlayerAdminCommand {

    public CustomItemCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Items item = getArgument(args, 0).toEnum(Items.class);

        if (true) {
            Chat.sendMessage(player, "&cCurrently disabled.");
            return;
        }

        if (item == null) {
            Chat.sendMessage(player, "&cUnknown item!");
            return;
        }

        //player.getInventory().addItem(item.getItem());
        Chat.sendMessage(player, "&aGave you 1 x %s!", item.name());
    }
}
