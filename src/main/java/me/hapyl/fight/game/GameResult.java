package me.hapyl.fight.game;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Set;

public class GameResult {

    private final GameInstance gameInstance;
    private final Set<GamePlayer> winners;
    private final Set<GameTeam> winningTeams;
    private final Set<GamePlayer> players;

    public GameResult(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        this.winners = Sets.newHashSet();
        this.winningTeams = Sets.newHashSet();
        this.players = CF.getPlayers();
    }

    @Nonnull
    public GameResultType getResultType() {
        return isWinners()
                ? isDraw() ? GameResultType.DRAW
                : isSingleWinner() ? GameResultType.SINGLE_WINNER : GameResultType.MULTIPLE_WINNERS
                : GameResultType.NO_WINNERS;
    }

    @Nonnull
    public GameInstance getGameInstance() {
        return gameInstance;
    }

    @Nonnull
    public Set<GamePlayer> getWinners() {
        return winners;
    }

    @Nonnull
    public Set<GameTeam> getWinningTeams() {
        return winningTeams;
    }

    @Nonnull
    public GameTeam getWinningTeam() {
        for (GameTeam winningTeam : winningTeams) {
            return winningTeam;
        }

        throw new NullPointerException("no winning teams?");
    }

    public boolean isDraw() {
        return winningTeams.size() >= 2;
    }

    public void awardWinners() {
        for (GamePlayer winner : winners) {
            Award.GAME_WON.award(winner);

            // Progress bond
            ChallengeType.WIN_GAMES.progress(winner);

            // Call skin
            winner.callSkinIfHas(skin -> skin.onWin(winner));
        }
    }

    @Nonnull
    public Set<GamePlayer> getPlayers() {
        return players;
    }

    public void calculate() {
        Chat.broadcast("");
        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        gameInstance.getMode().displayWinners(this);

        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        // Show each player their game report
        GameTask.runLater(() -> {
            for (GamePlayer gamePlayer : players) {
                final Player player = gamePlayer.getPlayer();
                final StatContainer stat = gamePlayer.getStats();

                Chat.sendMessage(player, "&a&lɢᴀᴍᴇ ʀᴇᴘᴏʀᴛ:");
                Chat.sendMessage(player, stat.getString(StatType.COINS));
                Chat.sendMessage(player, stat.getString(StatType.EXP));
                Chat.sendMessage(player, stat.getString(StatType.KILLS));
                Chat.sendMessage(player, stat.getString(StatType.DEATHS));
            }
        }, 20).setShutdownAction(ShutdownAction.IGNORE);
    }

    @Nonnull
    public String formatWinners() {
        final StringBuilder builder = new StringBuilder();

        int i = 0;
        final int size = winners.size();

        for (GamePlayer winner : winners) {
            final ChatColor teamColor = winner.getTeam().getColor();
            final String winnerNameColored = teamColor + winner.getName();

            if (isSingleWinner()) {
                return winnerNameColored;
            }

            if (i == size - 1) {
                builder.append(" &7and ");
            }
            else if (i != 0) {
                builder.append("&7, ");
            }

            builder.append(winnerNameColored);
            i++;
        }

        return builder.toString().trim();
    }

    @Nonnull
    public String getGameTimeFormatted() {
        return new SimpleDateFormat("mm:ss").format(System.currentTimeMillis() - gameInstance.getStartedAt());
    }

    public boolean isWinners() {
        return !winners.isEmpty();
    }

    public boolean isWinner(Player player) {
        for (GamePlayer winner : winners) {
            if (winner.compare(player)) {
                return true;
            }
        }

        return false;
    }

    public void supplyDefaultWinners() {
        CF.getAlivePlayers().forEach(player -> {
            winners.add(player);
            winningTeams.add(player.getTeam());
        });
    }

    private boolean isSingleWinner() {
        return winners.size() == 1;
    }

}
