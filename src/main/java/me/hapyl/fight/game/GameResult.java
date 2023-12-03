package me.hapyl.fight.game;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Set;

public class GameResult {

    private final GameInstance gameInstance;
    private final Set<GamePlayer> winners;
    private final Set<GameTeam> winningTeams;

    public GameResult(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        this.winners = Sets.newHashSet();
        this.winningTeams = Sets.newHashSet();
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public Set<GamePlayer> getWinners() {
        return winners;
    }

    public Set<GameTeam> getWinningTeams() {
        return winningTeams;
    }

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
        }
    }

    public void calculate() {
        final String gameDuration = new SimpleDateFormat("mm:ss").format(System.currentTimeMillis() - this.gameInstance.getStartedAt());

        // show the winners
        Chat.broadcast("");
        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        // Per player broadcast
        Bukkit.getOnlinePlayers().forEach(player -> {
            Chat.sendCenterMessage(player, "&a&lGAME OVER");
            Chat.sendCenterMessage(player, "&7" + gameDuration);
            Chat.sendMessage(player, "");

            if (isWinners()) {
                if (isDraw()) {
                    Chat.sendCenterMessage(player, "&b&lDRAW!");
                }
                else {
                    // Display either WINNER or WINNERS
                    Chat.sendCenterMessage(
                            player,
                            "&a&l%s",
                            isSingleWinner() ? "WINNER" : "WINNERS"
                    );
                }

                // Display winners
                for (GamePlayer winner : winners) {
                    // Display winner information to the player
                    Chat.sendCenterMessage(player, formatWinnerName(winner));
                }
            }
            else {
                Chat.sendCenterMessage(player, "&8No winners :(");
                Chat.sendTitle(player, "&6&lGAME OVER", "&eThere are no winners!", 10, 60, 5);
            }

            // Display TITLE
            if (isWinner(player)) {
                Chat.sendTitle(player, "&6&lVICTORY", "&eYou're the winner!", 10, 60, 5);
            }
            else {
                if (!isWinners()) {
                    Chat.sendTitle(player, "&6&lGAME OVER", "&eThere are no winners!", 10, 60, 5);
                }
                else {
                    Chat.sendTitle(
                            player,
                            "&c&lDEFEAT",
                            "&e%s %s the winner!".formatted(formatWinners(), isSingleWinner() ? "is" : "are"),
                            10,
                            60,
                            5
                    );
                }
            }
        });

        Chat.broadcast("&6&l▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

        // Show each player their game report
        GameTask.runLater(() -> {
            for (GamePlayer gamePlayer : gameInstance.getPlayers().values()) {
                final Player player = gamePlayer.getPlayer();
                final StatContainer stat = gamePlayer.getStats();

                // TODO: 028, Feb 28, 2023 -> Either automate report or actually add it every time.
                Chat.sendMessage(player, "&a&lGame Report:");
                Chat.sendMessage(player, stat.getString(StatType.COINS));
                Chat.sendMessage(player, stat.getString(StatType.EXP));
                Chat.sendMessage(player, stat.getString(StatType.KILLS));
                Chat.sendMessage(player, stat.getString(StatType.DEATHS));
            }
        }, 20).setShutdownAction(ShutdownAction.IGNORE);
    }

    public String formatWinners() {
        final StringBuilder builder = new StringBuilder();

        int i = 0;
        for (GamePlayer winner : winners) {
            if (isSingleWinner()) {
                return winner.getName();
            }

            if (i != 0) {
                builder.append(", ");
            }

            builder.append(winner.getName());
            i++;
        }

        return builder.toString().trim();
    }

    private boolean isSingleWinner() {
        return winners.size() == 1;
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

    public String formatWinnerName(GamePlayer winner) {
        final StatContainer stats = winner.getStats();
        @Nullable final GameTeam winnerTeam = winner.getTeam();

        return Chat.bformat(
                "{Team} &7⁑ &6{Hero} &e&l{Name} &7⁑ &c&l{Health} &c❤  &b&l{Kills} &b🗡  &c&l{Deaths} &c☠",
                (winnerTeam == null ? "" : winnerTeam.getFirstLetterCaps()),
                winner.getHero().getNameSmallCaps(),
                winner.getName(),
                winner.getHealthFormatted(),
                stats.getValue(StatType.KILLS),
                stats.getValue(StatType.DEATHS)
        );
    }

    public void supplyDefaultWinners() {
        winners.addAll(gameInstance.getAlivePlayers());
        for (GamePlayer winner : winners) {
            winningTeams.add(winner.getTeam());
        }
    }
}
