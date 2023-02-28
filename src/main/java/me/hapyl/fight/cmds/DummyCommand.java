package me.hapyl.fight.cmds;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.entity.Player;

public class DummyCommand extends SimplePlayerAdminCommand {
    public DummyCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        Entities.IRON_GOLEM.spawn(player.getLocation(), self -> {
            self.setMaxHealth(2048);
            self.setHealth(self.getMaxHealth());
            self.setCustomName(Chat.format("&aDummy"));
            self.setCustomNameVisible(true);
            self.setAI(false);
        });

        Chat.sendMessage(player, "&aSpawned dummy.");
    }
}
