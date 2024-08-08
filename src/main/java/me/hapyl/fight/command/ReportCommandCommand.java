package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class ReportCommandCommand extends SimplePlayerAdminCommand {

    public ReportCommandCommand(String str) {
        super(str);
    }

    @Override
    protected void execute(Player player, String[] args) {
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.openUrl("https://github.com/hapyl/CFArena/issues"),
                LazyEvent.showText("&eClick to open link!"),
                "&e&lCLICK HERE &ato report a bug on github."
        );
    }


}