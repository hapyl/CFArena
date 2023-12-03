package me.hapyl.fight.cmds;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.cmds.extra.Acceptor;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
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

                return super.completerSort(map.get(args.length), args);
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

                GamePlayer.getPlayer(player).heal(value);
                sendMessage(player, "&aHealed you for &l%s&a!", value);
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

                final GameTeam team = GameTeam.getPlayerTeam(player);

                if (team == null) {
                    sendError(player, "Nowhere to set kills! No game instance?");
                    return;
                }

                team.kills = newKills;
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

        acceptors.put("coins", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                // admin coins <set,add,remove|get> <player> <value>
                if (checkLength(args, 2)) {

                    final String action = args[0].toLowerCase();
                    final Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sendError(player, "&cInvalid player!");
                        return;
                    }

                    final CurrencyEntry currency = PlayerDatabase.getDatabase(target).getCurrency();

                    if (action.equalsIgnoreCase("get")) {
                        sendMessage(player, "&a%s has &l%s&a coins.", target.getName(), currency.get(Currency.COINS));
                        return;
                    }

                    if (checkLength(args, 3)) {
                        final long value = longValue(args, 2);

                        if (value < 0) {
                            sendError(player, "&cValue cannot be negative!");
                            return;
                        }

                        switch (action) {
                            case "set" -> {
                                currency.set(Currency.COINS, value);
                                sendMessage(player, "&aSet %s's coins to &l%s&a.", target.getName(), value);
                            }

                            case "add" -> {
                                currency.set(Currency.COINS, 1);
                                currency.add(Currency.COINS, value);
                                sendMessage(player, "&aAdded &l%s&a coins to %s.", value, target.getName());
                            }

                            case "remove" -> {
                                currency.subtract(Currency.COINS, value);
                                sendMessage(player, "&aRemoved &l%s&a coins from %s.", value, target.getName());
                            }

                            // FIXME (hapyl): 019, Apr 19, 2023: add rubies
                            default -> sendError(player, "&cInvalid action!");
                        }
                    }

                }
            }

            @Override
            public void createAdditionalArguments() {
                addArgument(2, "add", "set", "remove");
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
        Chat.sendMessage(player, PREFIX + "&4Error! &c" + message, format);
    }

    private void sendMessage(Player player, String message, Object... format) {
        Chat.sendMessage(player, PREFIX + message, format);
    }

}