package me.hapyl.fight.command;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.gui.CollectionGUI;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CosmeticCommand extends SimplePlayerCommand {
    public CosmeticCommand(String name) {
        super(name);

        setDescription("Allows previewing cosmetics.");
        addCompleterValues(1, "play", "set", "has", "give", "remove");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if ((args.length >= 1 && args[0].equalsIgnoreCase("play")) || args.length == 3) {
            return completerSort(Cosmetics.values(), args);
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

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "play" -> {
                    // cosmetic play <cosmetic>
                    final Cosmetics cosmetic = Validate.getEnumValue(Cosmetics.class, args[1]);
                    if (cosmetic == null) {
                        Chat.sendMessage(player, "&cInvalid cosmetic! &7Valid cosmetics: %s".formatted(Arrays.toString(Cosmetics.values())));
                        return;
                    }

                    cosmetic.getCosmetic().onDisplay0(new Display(player, player.getLocation()));
                    Chat.sendMessage(player, "&aDisplaying cosmetic %s".formatted(cosmetic.name()));
                }
                case "giveall" -> {
                    final Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        Notifier.Error.PLAYER_NOT_ONLINE.send(player, args[1]);
                        return;
                    }

                    final PlayerDatabase database = PlayerDatabase.getDatabase(target);
                    final CosmeticEntry cosmetics = database.cosmeticEntry;

                    for (Cosmetics value : Cosmetics.values()) {
                        cosmetics.addOwned(value);
                    }

                    Notifier.success(player, "Gave all cosmetics to {}.", target.getName());
                    Chat.sendMessage(target, "&aAn admin gave you all the cosmetics. Wow!");
                }
                case "removeall" -> {
                    final Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        Notifier.Error.PLAYER_NOT_ONLINE.send(player, args[1]);
                        return;
                    }

                    final PlayerDatabase database = PlayerDatabase.getDatabase(target);
                    final CosmeticEntry cosmetics = database.cosmeticEntry;

                    for (Cosmetics value : Cosmetics.values()) {
                        cosmetics.removeOwned(value);
                    }

                    Notifier.success(player, "Removed all cosmetics from {}.", target.getName());
                    Chat.sendMessage(target, "&aAn admin took away all your cosmetics.");
                }
            }
        }
        else if (args.length == 3) {
            final String action = args[0];
            final Player target = Bukkit.getPlayer(args[1]);
            final Cosmetics cosmetic = Validate.getEnumValue(Cosmetics.class, args[2]);

            if (target == null) {
                Chat.sendMessage(player, "&cInvalid player!");
                return;
            }

            if (cosmetic == null) {
                Chat.sendMessage(player, "&cInvalid cosmetic! &7Valid cosmetics: %s".formatted(Arrays.toString(Cosmetics.values())));
                return;
            }

            final PlayerDatabase playerDatabase = PlayerDatabase.getDatabase(target);
            final CosmeticEntry cosmetics = playerDatabase.cosmeticEntry;
            final Type cosmeticType = cosmetic.getType();

            final String cosmeticName = cosmetic.name();
            final String cosmeticTypeName = cosmeticType.name();

            switch (action) {
                case "set" -> {
                    if (cosmetics.getSelected(cosmeticType) == cosmetic) {
                        cosmetics.unsetSelected(cosmeticType);
                        Chat.sendMessage(player, "&aUnset %s's %s cosmetic!".formatted(target.getName(), cosmeticTypeName));
                        Chat.sendMessage(target, "&aAn admin unselected your %s.".formatted(cosmeticTypeName));
                        return;
                    }

                    cosmetics.setSelected(cosmeticType, cosmetic);
                    Chat.sendMessage(player, "&aSet %s's %s cosmetic to %s".formatted(target.getName(), cosmeticTypeName, cosmeticName));
                    Chat.sendMessage(target, "&aAn admin set your %s to %s.".formatted(cosmeticTypeName, cosmeticName));
                }

                case "has" -> {
                    final boolean hasCosmetic = cosmetics.hasCosmetic(cosmetic);
                    Chat.sendMessage(player, "&a%s %s %s!".formatted(target.getName(), hasCosmetic ? "has" : "does not have", cosmeticName));
                }

                case "give" -> {
                    if (cosmetics.hasCosmetic(cosmetic)) {
                        Chat.sendMessage(player, "&c%s already has %s!".formatted(target.getName(), cosmeticName));
                        return;
                    }

                    cosmetics.addOwned(cosmetic);
                    Chat.sendMessage(player, "&aGave %s to %s".formatted(cosmeticName, target.getName()));
                    Chat.sendMessage(target, "&aAn admin gave you %s.".formatted(cosmeticName));
                }

                case "remove" -> {
                    cosmetics.removeOwned(cosmetic);
                    Chat.sendMessage(player, "&aRemoved %s from %s".formatted(cosmeticName, target.getName()));
                    Chat.sendMessage(target, "&aAn admin removed %s from you.".formatted(cosmeticName));
                }

                default -> {
                    Chat.sendMessage(player, "&cInvalid action! &7Valid actions: set, has, give, remove");
                }
            }
        }
    }
}
