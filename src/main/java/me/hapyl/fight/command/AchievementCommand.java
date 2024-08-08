package me.hapyl.fight.command;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AchievementCommand extends SimplePlayerAdminCommand {

    private final AchievementRegistry registry;

    public AchievementCommand(String name) {
        super(name);
        setUsage("achievement (player) (give|revoke|get) (achievement)");
        setAliases("ach");

        registry = Main.getPlugin().getAchievementRegistry();

        addCompleterValues(2, "give", "revoke", "reset", "get");
        addCompleterValues(3, registry.listIds());
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length != 3) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        final Achievement achievement = registry.byId(args[2]);

        if (target == null) {
            Chat.sendMessage(player, "&cThis player is not online!");
            return;
        }

        if (achievement == null) {
            Chat.sendMessage(player, "&cThis achievement does not exist!");
            return;
        }

        final AchievementEntry database = PlayerDatabase.getDatabase(target).achievementEntry;

        switch (args[1].toLowerCase()) {
            case "give" -> {
                if (achievement.complete(target)) {
                    Chat.sendMessage(player, "&aSuccessfully gave achievement to %s!".formatted(target.getName()));
                    return;
                }

                Chat.sendMessage(player, "&cThis achievement is already completed!");
            }

            case "revoke" -> {
                final int completeCount = database.getCompleteCount(achievement);
                if (completeCount <= 0) {
                    Chat.sendMessage(player, "&c%s hasn't completed this achievement yet!".formatted(target.getName()));
                    return;
                }

                database.subtractCompleteCount(achievement);
                Chat.sendMessage(player, "&aSuccessfully revoked achievement from %s!".formatted(target.getName()));
            }

            case "reset" -> {
                database.reset(achievement);
                Chat.sendMessage(player, "&aSuccessfully reset achievement for %s!".formatted( target.getName()));
            }

            case "get" -> {
                final int completeCount = database.getCompleteCount(achievement);
                if (completeCount <= 0) {
                    Chat.sendMessage(player, "&c%s hasn't completed this achievement yet!".formatted(target.getName()));
                    return;
                }

                Chat.sendMessage(player, "&a%s has completed this achievement %d times!".formatted(target.getName(), completeCount));
            }
        }
    }
}
