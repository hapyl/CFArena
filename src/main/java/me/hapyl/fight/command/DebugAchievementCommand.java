package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

public class DebugAchievementCommand extends SimplePlayerAdminCommand {

    private final AchievementRegistry registry;

    public DebugAchievementCommand(String name) {
        super(name);
        setUsage("debugachievement (achievement)");

        registry = Registries.achievements();

        addCompleterValues(1, registry.listIds());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length != 1) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Achievement achievement = registry.get(args[0]);

        if (achievement == null) {
            Chat.sendMessage(player, "&cInvalid achievement!");
            return;
        }

        Chat.sendMessage(player, "&c&lDEBUG:");
        Chat.sendMessage(player, "&aType: &f" + achievement.getClass().getSimpleName());
        Chat.sendMessage(player, "&aName: &f" + achievement.getName());
        Chat.sendMessage(player, "&aDescription: &f" + achievement.getDescription());
        Chat.sendMessage(player, "&aRewards: " + achievement.getPointReward());

    }
}
