package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.*;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CosmeticCommand extends SimplePlayerCommand {
    public CosmeticCommand(String name) {
        super(name);

        setDescription("Cosmetic management command.");
        addCompleterValues(1, "play", "set", "has", "give", "remove");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if ((args.length >= 1 && args[0].equalsIgnoreCase("play")) || args.length == 3) {
            return completerSort(Registries.getCosmetics().keys(), args);
        }

        return null;
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            new CollectionGUI(player);
            return;
        }

        if (PlayerRank.getRank(player) != PlayerRank.ADMIN) {
            Chat.sendMessage(player, "&cYou do not have permission to use this command!");
            return;
        }

        final CosmeticRegistry registry = Registries.getCosmetics();

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "play" -> {
                    // cosmetic play <cosmetic>
                    final Cosmetic cosmetic = registry.get(args[1]);

                    if (cosmetic == null) {
                        Message.error(player, "Unknown cosmetic: {%s}".formatted(args[1]));
                        return;
                    }

                    cosmetic.onDisplay0(new Display(player, player.getLocation()));

                    Message.success(player, "Displaying cosmetic: {%s}".formatted(cosmetic.getName()));
                }
                case "giveall" -> {
                    final Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        Message.Error.PLAYER_NOT_ONLINE.send(player, args[1]);
                        return;
                    }

                    for (Cosmetic cosmetic : registry.values()) {
                        cosmetic.setUnlocked(target, true);
                    }

                    Message.success(player, "Gave all cosmetics to {%s}.".formatted(target.getName()));
                    Message.success(target, "An admin gave you all the cosmetics. Wow!");
                }
                case "removeall" -> {
                    final Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        Message.Error.PLAYER_NOT_ONLINE.send(player, args[1]);
                        return;
                    }

                    for (Cosmetic cosmetic : registry.values()) {
                        cosmetic.setUnlocked(target, false);
                    }

                    Message.success(player, "Removed all cosmetics from {%s}.".formatted(target.getName()));
                    Message.success(target, "An admin took away all your cosmetics!");
                }
            }
        }
        else if (args.length == 3) {
            final String action = args[0];
            final Player target = Bukkit.getPlayer(args[1]);
            final Cosmetic cosmetic = registry.get(args[2]);

            if (target == null) {
                Message.error(player, "Unknown player: {%s}".formatted(args[1]));
                return;
            }

            if (cosmetic == null) {
                Message.error(player, "Unknown cosmetic: {%s}".formatted(args[2]));
                return;
            }

            final CosmeticEntry entry = CF.getDatabase(target).cosmeticEntry;
            final Type cosmeticType = cosmetic.getType();

            final String cosmeticName = cosmetic.getName();
            final String cosmeticTypeName = cosmeticType.getName();

            final String targetName = target.getName();

            switch (action) {
                case "set" -> {
                    if (entry.getSelected(cosmeticType) == cosmetic) {
                        entry.unsetSelected(cosmeticType);

                        Message.success(player, "Unset {%s}'s {%s} cosmetic!".formatted(targetName, cosmeticTypeName));
                        return;
                    }

                    entry.setSelected(cosmeticType, cosmetic);

                    Message.success(
                            player,
                            "Set {%s}'s {%s} cosmetic to {%s}!".formatted(targetName, cosmeticTypeName, cosmeticName)
                    );
                }

                case "has" -> {
                    final boolean hasCosmetic = entry.isUnlocked(cosmetic);

                    Message.success(
                            player,
                            "{%s} {%s} {%s}!".formatted(targetName, hasCosmetic ? "has" : "does not have", cosmeticName)
                    );
                }

                case "give" -> {
                    if (entry.isUnlocked(cosmetic)) {
                        Message.error(player, "{%s} already has {%s}!".formatted(targetName, cosmeticName));
                        return;
                    }

                    entry.setUnlocked(cosmetic, true);

                    Message.success(player, "Gave {%s} to {%s}!".formatted(cosmeticName, targetName));
                }

                case "remove" -> {
                    if (!entry.isUnlocked(cosmetic)) {
                        Message.error(player, "{%s} doesn't have {%s}!".formatted(targetName, cosmeticName));
                        return;
                    }

                    entry.setUnlocked(cosmetic, false);

                    Message.success(player, "Removed {%s} from {%s}!".formatted(cosmeticName, targetName));
                }

                default -> Message.error(player, "Invalid usage!");
            }
        }
    }
}
