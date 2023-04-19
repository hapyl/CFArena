package me.hapyl.fight.cmds;

import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

public class DebugAchievementCommand extends SimplePlayerAdminCommand {
    public DebugAchievementCommand(String name) {
        super(name);
        setUsage("debugachievement (achievement)");

        addCompleterValues(1, Achievements.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length != 1) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Achievements enumAchievement = Validate.getEnumValue(Achievements.class, args[0]);

        if (enumAchievement == null) {
            Chat.sendMessage(player, "&cInvalid achievement!");
            return;
        }

        final Achievement achievement = enumAchievement.getAchievement();

        Chat.sendMessage(player, "&c&lDEBUG:");
        Chat.sendMessage(player, "&aType: &f" + achievement.getClass().getSimpleName());
        Chat.sendMessage(player, "&aName: &f" + achievement.getName());
        Chat.sendMessage(player, "&aDescription: &f" + achievement.getDescription());
        Chat.sendMessage(player, "&aRewards: " + achievement.getRewards());

    }
}
