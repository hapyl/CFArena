package me.hapyl.fight.cmds;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AchievementCommand extends SimplePlayerAdminCommand {
    public AchievementCommand(String name) {
        super(name);
        setUsage("achievement (player) (give|revoke|get) (achievement)");
        setAliases("ach");

        addCompleterValues(2, "give", "revoke", "reset", "get");
        addCompleterValues(3, Achievements.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length != 3) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        final Achievements achievement = Validate.getEnumValue(Achievements.class, args[2]);

        if (target == null) {
            Chat.sendMessage(player, "&cThis player is not online!");
            return;
        }

        if (achievement == null) {
            Chat.sendMessage(player, "&cThis achievement does not exist!");
            return;
        }

        final AchievementEntry database = PlayerDatabase.getDatabase(target).getAchievementEntry();
        switch (args[1].toLowerCase()) {
            case "give" -> {
                if (achievement.complete(target)) {
                    Chat.sendMessage(player, "&aSuccessfully gave achievement to %s!", target.getName());
                    return;
                }

                Chat.sendMessage(player, "&cThis achievement is already completed!");
            }

            case "revoke" -> {
                final int completeCount = database.getCompleteCount(achievement);
                if (completeCount <= 0) {
                    Chat.sendMessage(player, "&c%s hasn't completed this achievement yet!", target.getName());
                    return;
                }

                database.subtractCompleteCount(achievement);
                Chat.sendMessage(player, "&aSuccessfully revoked achievement from %s!", target.getName());
            }

            case "reset" -> {
                database.reset(achievement);
                Chat.sendMessage(player, "&aSuccessfully reset achievement for %s!", target.getName());
            }

            case "get" -> {
                final int completeCount = database.getCompleteCount(achievement);
                if (completeCount <= 0) {
                    Chat.sendMessage(player, "&c%s hasn't completed this achievement yet!", target.getName());
                    return;
                }

                Chat.sendMessage(player, "&a%s has completed this achievement %d times!", target.getName(), completeCount);
            }
        }
    }
}
