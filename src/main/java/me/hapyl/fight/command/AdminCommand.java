package me.hapyl.fight.command;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.command.extra.Acceptor;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class AdminCommand extends SimplePlayerAdminCommand {

    private final static String PREFIX = "&c&lADMIN &e";
    private final Map<String, Acceptor> acceptors;

    public AdminCommand(String str) {
        super(str);
        setUsage("/admin <operation> (Value...)");
        setDescription("Admin management command.");

        acceptors = Maps.newHashMap();
        initAcceptors();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!PlayerRank.getRank(sender).isOrHigher(PlayerRank.ADMIN)) {
            return List.of("§cYou are not administrator!");
        }

        if (args.length == 1) {
            return super.completerSort(acceptors.keySet().stream().toList(), args);
        }
        else {
            for (String leader : acceptors.keySet()) {
                if (!leader.equalsIgnoreCase(args[0])) {
                    continue;
                }

                final Acceptor acceptor = acceptors.get(leader);
                final Map<Integer, List<String>> map = acceptor.additionalArguments();

                if (map.isEmpty()) {
                    return null;
                }

                final List<String> list = map.get(args.length);

                if (list == null) {
                    return null;
                }

                return super.completerSort(list, args);
            }
        }
        return null;
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (!PlayerRank.getRank(player).isOrHigher(PlayerRank.ADMIN)) {
            sendError(player, "You are not an administrator!");
            return;
        }

        final Acceptor acceptor = acceptors.get(args[0].toLowerCase(Locale.ROOT));

        if (acceptor != null) {
            final String[] strings = new String[args.length - 1];

            System.arraycopy(args, 1, strings, 0, args.length - 1);
            acceptor.execute(player, strings);
        }
    }

    private void initAcceptors() {
        acceptors.put("damage", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    sendError(player, "Excepted a double, got nothing.");
                    return;
                }

                final double value = doubleValue(args, 0);

                if (value < 0) {
                    sendError(player, "Expected a positive value, got negative.");
                    return;
                }

                CF.getPlayerOptional(player).ifPresentOrElse(gamePlayer -> {
                    gamePlayer.damage(value, player);
                    sendMessage(player, "&aDealt &l%s&a damage to you!", value);
                }, () -> {
                    sendMessage(player, "&cCannot find game player instance.");
                });
            }
        });

        acceptors.put("heal", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    sendError(player, "Expected a double, got nothing.");
                    return;
                }

                final double value = doubleValue(args, 0);

                if (value < 0) {
                    sendError(player, "Expected a positive value, got negative.");
                    return;
                }

                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    player.sendMessage("&cNo handle!");
                    return;
                }

                gamePlayer.heal(value);
                gamePlayer.sendMessage("&aHealed you for &l%s&a!", value);
            }
        });

        acceptors.put("setkills", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    return;
                }

                final int newKills = intValue(args, 0);

                if (newKills < 0) {
                    sendError(player, "Expected a positive value, got negative.");
                    return;
                }

                final GameTeam team = GameTeam.getEntryTeam(Entry.of(player));

                if (team == null) {
                    sendError(player, "Nowhere to set kills! No game instance?");
                    return;
                }

                team.data.kills = newKills;
                sendMessage(player, "&aSet your team kills to &l%s&a.", newKills);
            }
        });

        acceptors.put("map", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    return;
                }

                final GameMaps map = Validate.getEnumValue(GameMaps.class, args[0]);
                if (map == null) {
                    sendMessage(player, "&cInvalid map!");
                    return;
                }

                player.teleport(map.getMap().getLocation());
                sendMessage(player, "&aTeleported to %s map!", map.getName());
            }

            @Override
            public void createAdditionalArguments() {
                for (GameMaps value : GameMaps.values()) {
                    addArgument(2, value.name().toLowerCase(Locale.ROOT));
                }
            }
        });

        acceptors.put("currency", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                // admin currency <currency> <player> <set,add,remove,get> [value]
                final Currency currency = getArgument(args, 0).toEnum(Currency.class);
                final Player target = Bukkit.getPlayer(getArgument(args, 1).toString());
                final String argument = getArgument(args, 2).toString();
                final long value = getArgument(args, 3).toLong();

                if (currency == null) {
                    Chat.sendMessage(player, "&cInvalid currency!");
                    return;
                }

                if (target == null) {
                    Chat.sendMessage(player, "&cThis player is not online!");
                    return;
                }

                final CurrencyEntry currencyEntry = CF.getDatabase(target).currencyEntry;
                final String currencyFormatted = currency.getFormatted();

                if (argument.equalsIgnoreCase("get")) {
                    final long targetValue = currencyEntry.get(currency);

                    Chat.sendMessage(player, "&a%s has %,d %s.".formatted(target.getName(), targetValue, currencyFormatted));
                    return;
                }

                switch (argument.toLowerCase()) {
                    case "set" -> {
                        currencyEntry.set(currency, value);
                        sendMessage(player, "&aSet %s's %s to &l%s&a.", target.getName(), currencyFormatted, value);
                        Chat.sendMessage(target, "&aAn admin set your %s to %s.".formatted(currencyFormatted, value));
                    }

                    case "add" -> {
                        currencyEntry.add(currency, value);
                        sendMessage(player, "&aAdded &l%s&a %s to %s.", value, currencyFormatted, target.getName());
                        Chat.sendMessage(target, "&aAn admin gave you %s %s.".formatted(currencyFormatted, value));
                    }

                    case "remove" -> {
                        currencyEntry.subtract(currency, value);
                        sendMessage(player, "&aRemoved &l%s&a %s from %s.", value, currencyFormatted, target.getName());
                        Chat.sendMessage(target, "&aAn admin removed %s %s from you.".formatted(value, currencyFormatted));
                    }

                    default -> sendError(player, "&cInvalid arguments!");
                }
            }

            @Override
            public void createAdditionalArguments() {
                for (Currency currency : Currency.values()) {
                    addArgument(2, currency.name().toLowerCase());
                }

                addArgument(4, "get", "set", "add", "remove");
            }
        });

    }

    @Nullable
    private <T> T fromArgsRaw(@Nonnull String[] args, int index, @Nonnull Function<String, T> functions, @Nullable T def) {
        if (index >= args.length) {
            return def;
        }

        return functions.apply(args[index]);
    }

    private void sendError(Player player, String message, Object... format) {
        Chat.sendMessage(player, (PREFIX + "&4Error! &c" + message).formatted(format));
    }

    private void sendMessage(Player player, String message, Object... format) {
        Chat.sendMessage(player, (PREFIX + message).formatted(format));
    }

}