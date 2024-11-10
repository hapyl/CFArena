package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.entry.ExperienceEntry;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.experience.ExperienceDebugGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExperienceCommand extends SimplePlayerAdminCommand {
    public ExperienceCommand(String name) {
        super(name);
        setAliases("exp");
        addCompleterValues(2, "set", "add", "remove", "fix", "reset");
        addCompleterValues(3, ExperienceEntry.Type.values());
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Experience experience = Main.getPlugin().getExperience();

        if (args.length == 0) {
            new ExperienceDebugGUI(player);
            return;
        }

        if (args.length == 2) {
            final String arg = args[1].toLowerCase();
            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Chat.sendMessage(player, "&cInvalid player.");
                return;
            }

            final ExperienceEntry database = experience.getDatabaseEntry(target);

            switch (arg) {
                case "fix" -> {
                    experience.fixRewards(target);
                    Chat.sendMessage(player, "&aFixing rewards for %s...".formatted(target.getName()));
                }

                case "reset" -> {
                    database.reset(ExperienceEntry.Type.EXP);
                    database.reset(ExperienceEntry.Type.LEVEL);
                    database.reset(ExperienceEntry.Type.POINT);
                    experience.triggerUpdate(target);
                    Chat.sendMessage(player, "&aReset player.");
                }

                case "levelup" -> {
                    experience.levelUp(target, false);
                    Chat.sendMessage(player, "&aLevelling up %s...".formatted(target.getName()));
                }
            }

            return;
        }

        if (args.length < 4) {
            sendInvalidUsageMessage(player);
            return;
        }

        // exp (player) (set/add/remove/fix) (exp,lvl) (value)

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(player, "&cInvalid player.");
            return;
        }

        final ExperienceEntry database = experience.getDatabaseEntry(target);

        final String operation = args[1];
        final ExperienceEntry.Type type = Enums.byName(ExperienceEntry.Type.class, args[2]);
        final long value = Numbers.getLong(args[3], 0L);

        if (type == null) {
            Chat.sendMessage(player, "&cInvalid type.");
            return;
        }

        if (value < 0) {
            Chat.sendMessage(player, "&cValue cannot be negative.");
            return;
        }

        switch (operation) {
            case "set" -> {
                database.set(type, value);
                experience.triggerUpdate(target);

                if (type == ExperienceEntry.Type.LEVEL) {

                }

                Chat.sendMessage(player, "&aSet %s's experience %s to %s.".formatted(target.getName(), type.getName(), value));
            }

            case "add" -> {
                database.add(type, value);
                experience.triggerUpdate(target);
                Chat.sendMessage(player, "&aAdded %s experience %s for %s.".formatted(type.getName(), value, target.getName()));
            }

            case "remove" -> {
                database.remove(type, value);
                experience.triggerUpdate(target);
                Chat.sendMessage(player, "&aRemoved %s experience %s for %s.".formatted(type.getName(), value, target.getName()));
            }

            default -> Chat.sendMessage(player, "&cInvalid operation.");
        }
    }

}
