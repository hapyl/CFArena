package me.hapyl.fight.cmds;

import me.hapyl.fight.exception.ClassesFightException;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;

public class ThrowExceptionCommand extends SimplePlayerAdminCommand {
    public ThrowExceptionCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        throw new ClassesFightException("test exception");
    }
}
