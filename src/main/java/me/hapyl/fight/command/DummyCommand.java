package me.hapyl.fight.command;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.LivingGameEntity;
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
        final LivingGameEntity entity = CF.createEntity(player.getLocation(), Entities.IRON_GOLEM, self -> {
            self.setMaxHealth(2048);
            self.setHealth(self.getMaxHealth());
            self.setCustomName(Chat.format("&aDummy"));
            self.setCustomNameVisible(true);
            self.setAI(false);

            return new LivingGameEntity(self);
        });

        entity.setForceValid(true);

        Chat.sendMessage(player, "&aSpawned dummy.");
    }

}
