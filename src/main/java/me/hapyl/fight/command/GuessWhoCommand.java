package me.hapyl.fight.command;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.fight.util.PlayerInvite;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GuessWhoCommand extends SimplePlayerCommand {

    public GuessWhoCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Manager manager = Manager.current();

        // Since the reopening breaks everything making manual.
        // Just don't close the GUI lol!
        if (args.length == 0) {
            final GuessWho game = manager.getGuessWhoGame();

            if (game == null) {
                Message.error(player, "Provide a valid player name!");
                return;
            }

            final GuessWhoPlayer gamePlayer = game.getPlayer(player);

            if (gamePlayer == null) {
                return;
            }

            gamePlayer.promptSelectHero();
            return;
        }

        if (manager.isGuessWhoGameInProgress()) {
            Message.error(player, "A guess who game is already in progress!");
            return;
        }

        // guessWho (uuid) (accept, decline)
        // guessWho (player)

        if (args.length == 1) {
            final Player target = getArgument(args, 0).toPlayer();

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
