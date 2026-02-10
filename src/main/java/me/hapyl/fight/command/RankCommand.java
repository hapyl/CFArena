package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.annotate.NowListenToMe;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.security.SecureRandom;

@NowListenToMe("Don't make this CFCommand")
// Don't make this CFCommand !!!
// Don't make this CFCommand !!!
// Don't make this CFCommand !!!
public final class RankCommand extends SimpleCommand {

    private static final PlayerRank MIN_RANK = PlayerRank.ADMIN;

    private static final int HASH_LENGTH = 12;

    private static final long CONFIRM_TIMEOUT = 10_000;
    private static final Cache<RankConfirmation> CONFIRM_CACHE = Cache.ofSet(CONFIRM_TIMEOUT);
    private static final String HASH_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public RankCommand(String name) {
        super(name);

        setUsage("rank (player) [rank]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        final PlayerRank rank = PlayerRank.getRank(sender);

        if (!rank.isOrHigher(MIN_RANK)) {
            Message.Error.NOT_PERMISSIONS_NEED_RANK.send(sender, MIN_RANK.getPrefixWithFallback());
            return;
        }

        if (args.length == 0) {
            Message.success(sender, "Your rank is {%s}!".formatted(rank.getPrefixWithFallback()));
            return;
        }

        // Confirmation
        if (args.length == 1) {
            final String hash = getArgument(args, 0).toString();

            if (hash.length() != HASH_LENGTH) {
                Message.error(sender, "Invalid usage!");
                return;
            }

            final RankConfirmation rankConfirmation = CONFIRM_CACHE.match(rc -> rc.hash.equals(hash));

            if (rankConfirmation == null) {
                Message.error(sender, "Confirmation for '{%s}' has expired or doesn't exist!".formatted(hash));
                return;
            }

            CONFIRM_CACHE.remove(rankConfirmation);
            rankConfirmation.run();

            return;
        }

        final Player target = getArgument(args, 0).toPlayer();
        final PlayerRank rankToSet = getArgument(args, 1).toEnum(PlayerRank.class);

        if (target == null) {
            Message.error(sender, "{%s} is not online!".formatted(args[0]));
            return;
        }

        final PlayerDatabase targetDatabase = CF.getDatabase(target);
        final PlayerRank targetRank = targetDatabase.getRank();

        if (rankToSet == targetRank) {
            Message.error(sender, "{%s}'s rank is already {%s}!".formatted(target.getName(), targetRank.getPrefixWithFallback()));
            return;
        }

        if (rankToSet == null) {
            Message.success(sender, "{%s}'s rank is {%s}.".formatted(target.getName(), targetRank.getPrefixWithFallback()));
            return;
        }

        if (rankToSet.isStaff()) {
            final RankConfirmation rankConfirmation = CONFIRM_CACHE.match(rc -> rc.target.equals(target));

            if (rankConfirmation != null) {
                Message.error(sender, "There is already a rank confirmation request for this target!");
                return;
            }

            final RankConfirmation confirmation = new RankConfirmation(generateHash(), sender, target, rankToSet);

            CONFIRM_CACHE.add(confirmation);

            Message.success(
                    sender,
                    "Created request to set &e{%s}'s rank to &c{%s}.".formatted(target.getName(), rankToSet.getPrefixWithFallback())
            );
            Message.success(sender, "Because it's a staff rank, it requires a confirmation.");
            Message.success(sender, "Run &e/rank &e{%s} within &b{%s}s to confirm!".formatted(confirmation.hash, CONFIRM_TIMEOUT / 1000L));
            return;
        }

        doSetRankNowSkipChecks(sender, target, rankToSet);
    }

    private static void doSetRankNowSkipChecks(CommandSender sender, Player target, PlayerRank rankToSet) {
        if (target == null || !target.isOnline()) {
            Message.error(sender, "Target is no longer online!");
            return;
        }

        final PlayerDatabase database = CF.getDatabase(target);
        final PlayerRank oldRank = database.getRank();

        database.setRank(rankToSet);

        Message.success(sender, "Set &a{%s}'s rank to {%s}!".formatted(target.getName(), rankToSet.getPrefixWithFallback()));
        Message.success(target, "You are now {%s}!".formatted(rankToSet.getPrefixWithFallback()));

        Message.broadcastStaff("{%s} changed {%s}'s rank '{%s}' » '{%s}'.".formatted(
                sender.getName(),
                target.getName(),
                oldRank.getPrefixWithFallback(),
                rankToSet.getPrefixWithFallback()
        ));
    }

    @Nonnull
    private static String generateHash() {
        final SecureRandom random = new SecureRandom();
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < HASH_LENGTH; i++) {
            builder.append(HASH_CHARS.charAt(random.nextInt(HASH_CHARS.length())));
        }

        return builder.toString();
    }

    private record RankConfirmation(String hash, CommandSender requester, Player target, PlayerRank rank) implements Runnable {
        @Override
        public String toString() {
            return hash;
        }

        @Override
        public void run() {
            doSetRankNowSkipChecks(requester, target, rank);
        }
    }

}
