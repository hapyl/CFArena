package me.hapyl.fight.command;

import me.hapyl.fight.annotate.NowListenToMe;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.util.collection.CacheSet;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.command.SimpleCommand;
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
    private static final CacheSet<RankConfirmation> CONFIRM_CACHE = new CacheSet<>(CONFIRM_TIMEOUT);
    private static final String HASH_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public RankCommand(String name) {
        super(name);

        setUsage("rank (player) [rank]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        final PlayerRank rank = PlayerRank.getRank(sender);

        if (!rank.isOrHigher(MIN_RANK)) {
            Notifier.Error.NOT_PERMISSIONS_NEED_RANK.send(sender, MIN_RANK.getPrefixWithFallback());
            return;
        }

        if (args.length == 0) {
            Notifier.success(sender, "Your rank is {}!", rank.getPrefixWithFallback());
            return;
        }

        // Confirmation
        if (args.length == 1) {
            final String hash = getArgument(args, 0).toString();

            if (hash.length() != HASH_LENGTH) {
                Notifier.error(sender, "Invalid usage!");
                return;
            }

            final RankConfirmation rankConfirmation = CONFIRM_CACHE.findFirst(rc -> rc.hash.equals(hash));

            if (rankConfirmation == null) {
                Notifier.error(sender, "Confirmation for '{}' has expired or doesn't exist!", hash);
                return;
            }

            CONFIRM_CACHE.remove(rankConfirmation);
            rankConfirmation.run();

            return;
        }

        final Player target = getArgument(args, 0).toPlayer();
        final PlayerRank rankToSet = getArgument(args, 1).toEnum(PlayerRank.class);

        if (target == null) {
            Notifier.error(sender, "%s is not online!", args[0]);
            return;
        }

        final PlayerDatabase targetDatabase = PlayerDatabase.getDatabase(target);
        final PlayerRank targetRank = targetDatabase.getRank();

        if (rankToSet == targetRank) {
            Notifier.error(sender, "{}'s rank is already {}!", target.getName(), targetRank.getPrefixWithFallback());
            return;
        }

        if (rankToSet == null) {
            Notifier.success(sender, "{}'s rank is {}.", target.getName(), targetRank.getPrefixWithFallback());
            return;
        }

        if (rankToSet.isStaff()) {
            final RankConfirmation rankConfirmation = CONFIRM_CACHE.findFirst(rc -> rc.target.equals(target));

            if (rankConfirmation != null) {
                Notifier.error(sender, "There is already a rank confirmation request for this target!");
                return;
            }

            final RankConfirmation confirmation = new RankConfirmation(generateHash(), sender, target, rankToSet);

            CONFIRM_CACHE.add(confirmation);

            Notifier.success(sender, "Created request to set &e{}'s rank to &c{}.", target.getName(), rankToSet.getPrefixWithFallback());
            Notifier.success(sender, "Because it's a staff rank, it requires a confirmation.");
            Notifier.success(sender, "Run &e/rank &e{} within &b{}s to confirm!", confirmation.hash, CONFIRM_TIMEOUT / 1000L);
            return;
        }

        doSetRankNowSkipChecks(sender, target, rankToSet);
    }

    private static void doSetRankNowSkipChecks(CommandSender sender, Player target, PlayerRank rankToSet) {
        if (target == null || !target.isOnline()) {
            Notifier.error(sender, "Target is no longer online!");
            return;
        }

        final PlayerDatabase database = PlayerDatabase.getDatabase(target);
        final PlayerRank oldRank = database.getRank();

        database.setRank(rankToSet);

        Notifier.success(sender, "Set &a{}'s rank to {}!", target.getName(), rankToSet.getPrefixWithFallback());
        Notifier.success(target, "You are now {}!", rankToSet.getPrefixWithFallback());

        Notifier.broadcastStaff(
                "{} changed {}'s rank '{}&b' » '{}&b'.",
                sender.getName(),
                target.getName(),
                oldRank.getPrefixWithFallback(),
                rankToSet.getPrefixWithFallback()
        );
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
