package me.hapyl.fight.command;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.GuessWhoEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.fight.util.PlayerInvite;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GuessWhoCommand extends SimplePlayerCommand {

    public GuessWhoCommand(@Nonnull String name) {
        super(name);

        setAliases("gw", "who");
        setUsage("/guessWho [player, stats]");

        addCompleterValues(1, "stats");
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Manager manager = Manager.current();

        // Since the reopening breaks everything making manual.
        // Just don't close the GUI lol!
        if (args.length == 0) {
            final GuessWho game = manager.getGuessWhoGame();

            if (game == null) {
                sendInvalidUsageMessage(player);
                return;
            }

            final GuessWhoPlayer gamePlayer = game.getPlayer(player);

            if (gamePlayer == null) {
                return;
            }

            gamePlayer.promptSelectHero();
            return;
        }

        if (args.length == 1) {
            final String stringArgument = getArgument(args, 0).toString();

            if (stringArgument.equals("stats")) {
                final PlayerDatabase database = PlayerDatabase.getDatabase(player);
                final GuessWhoEntry entry = database.guessWhoEntry;

                Message.success(player, "Your GuessWho stats:");
                Message.info(player, " &aTotal Wins: %s".formatted(entry.getStat(GuessWhoEntry.StatType.WINS)));
                Message.info(player, " &aTotal Loses: %s".formatted(entry.getStat(GuessWhoEntry.StatType.LOSES)));
                Message.info(player, " &aTotal Forfeits: %s".formatted(entry.getStat(GuessWhoEntry.StatType.FORFEITS)));
                Message.info(player, " &aWin Streak: %s".formatted(entry.getStat(GuessWhoEntry.StatType.WIN_STREAK)));

                PlayerLib.plingNote(player, 2.0f);
                return;
            }

            final Player target = getArgument(args, 0).toPlayer();

            // TODO (hapyl): 018, Feb 18: I might allow multiple instances later but I'll think about it
            if (manager.isGuessWhoGameInProgress()) {
                Message.error(player, "A guess who game is already in progress!");
                return;
            }

            if (target == null) {
                Message.error(player, "This player is not online!");
                return;
            }

            if (target == player) {
                Message.error(player, "You cannot invite yourself, weirdo.");
                return;
            }

            final PlayerRank playerRank = PlayerRank.getRank(player);
            final PlayerRank targetRank = PlayerRank.getRank(target);

            if (targetRank.isStaff() && !playerRank.isStaff()) {
                Message.error(player, "You cannot invite this player!");
                return;
            }

            final PlayerInvite existingInvite = PlayerInvite.byUUID(player.getUniqueId());

            if (existingInvite != null) {
                Message.error(player, "You already have an outgoing invite!");
                return;
            }

            new PlayerInvite(player, target, "play a game of Guess Who") {
                @Override
                public void onAccept() {
                    if (manager.isGuessWhoGameInProgress()) {
                        Message.error(player, "A guess who game is already in progress!");
                        return;
                    }

                    manager.createNewGuessWhoGame(player, target);
                }

                @Override
                public void onDecline() {
                    Message.error(player, "{} has declined you invite.", target.getName());
                }
            };
        }
    }
}
