package me.hapyl.fight.cmds;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.LazyEvent;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class ReportCommandCommand extends SimplePlayerAdminCommand {

    public ReportCommandCommand(String str) {
        super(str);
    }

    @Override
    protected void execute(Player player, String[] args) {
        Chat.sendClickableHoverableMessage(
                player,
                LazyEvent.openUrl("https://github.com/hapyl/ClassesFightArena/issues"),
                LazyEvent.showText("&eClick to open link!"),
                "&e&lCLICK HERE &ato report a bug on github."
        );
    }


}