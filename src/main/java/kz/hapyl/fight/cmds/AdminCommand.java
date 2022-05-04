package kz.hapyl.fight.cmds;

import kz.hapyl.fight.cmds.extra.Acceptor;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.StatContainer;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminCommand extends SimplePlayerAdminCommand {

    private final Map<String, Acceptor> acceptors;

    public AdminCommand(String str) {
        super(str);
        this.setUsage("/admin [damage, setkills, map] (Value)");

        acceptors = new HashMap<>();
        initAcceptors();
    }

    private void initAcceptors() {

        acceptors.put("damage", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    return;
                }

                final double value = doubleValue(args, 0);
                GamePlayer.damageEntity(player, value);
                sendMessage(player, "&aDealt &l%s&a damage to you!", value);

            }
        });

        acceptors.put("setkills", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                if (!checkLength(args, 1)) {
                    return;
                }

                final long newKills = longValue(args, 0);
                if (newKills < 0) {
                    sendMessage(player, "&cKills cannot be negative.");
                    return;
                }

                final StatContainer stats = GamePlayer.getPlayer(player).getStats();
                if (stats == null) {
                    sendMessage(player, "&cStats are null! Game is not in progress?");
                    return;
                }

                stats.setValue(StatContainer.Type.KILLS, newKills);
                sendMessage(player, "&aSet your kills to &l%s&a.", newKills);
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
                sendMessage(player, "&aTeleported to %s!", map.getName());
            }
        });

        acceptors.put("coins", new Acceptor() {
            @Override
            public void execute(Player player, String[] args) {
                // set
                if (!checkLength(args, 2)) {
                    return;
                }

            }

            @Override
            public void createAdditionalArguments() {
                addArgument(1, "add", "set", "remove");
            }
        });

    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 0) {
            Chat.sendMessage(player, "Admin GUI not yet implemented!");
            sendInvalidUsageMessage(player);
            return;
        }

        final Acceptor acceptor = acceptors.get(args[0].toLowerCase(Locale.ROOT));
        if (acceptor != null) {
            final String[] strings = new String[args.length - 1];

            System.arraycopy(args, 1, strings, 0, args.length - 1);
            acceptor.execute(player, strings);
        }

    }

    private void sendMessage(Player player, String message, Object... objects) {
        Chat.sendMessage(player, "&c&lADMIN&7: " + message, objects);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return super.completerSort(acceptors.keySet().stream().toList(), args);
        }
        else {
            for (String leader : acceptors.keySet()) {
                if (!leader.equalsIgnoreCase(args[0])) {
                    return null;
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

}