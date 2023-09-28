package me.hapyl.fight.command;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimpleAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class RankCommand extends SimpleAdminCommand {

    private final static String ARGUMENT_SET_ADMIN = "-ConfirmSetAdmin";

    public RankCommand(String name) {
        super(name);

        setUsage("rank (player) [rank]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!PlayerRank.getRank(sender).isOrHigher(PlayerRank.ADMIN)) {
            Message.Error.NOT_PERMISSIONS_NEED_RANK.send(sender, PlayerRank.ADMIN);
            return;
        }

        if (args.length == 0) {
            Chat.sendMessage(sender, "&aYour rank is %s.", PlayerRank.getRank(sender));
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        @Nullable final PlayerRank rankToSet = args.length >= 2 ? Validate.getEnumValue(PlayerRank.class, args[1].toUpperCase()) : null;
        final boolean confirmSetAdmin = args.length >= 3 && args[2].equals(ARGUMENT_SET_ADMIN);

        if (target == null) {
            Chat.sendMessage(sender, "&c%s is not online.", args[0]);
            return;
        }

        final PlayerDatabase playerDatabase = PlayerDatabase.getDatabase(target);

        if (rankToSet == null) {
            final PlayerRank playerRank = playerDatabase.getRank();

            Chat.sendMessage(sender, "&a%s's rank is %s.", target.getName(), playerRank);
            return;
        }

        if (rankToSet.isStaff() && !confirmSetAdmin) {
            Chat.sendMessage(sender, "&cNot making &e%s&c administrator without &e%s&c argument!", target.getName(), ARGUMENT_SET_ADMIN);
            return;
        }

        playerDatabase.setRank(rankToSet);

        Chat.sendMessage(sender, "&aSet %s's rank to %s.", target.getName(), rankToSet);
        Chat.sendMessage(target, "&aYou are now %s.", rankToSet);

    }

}
