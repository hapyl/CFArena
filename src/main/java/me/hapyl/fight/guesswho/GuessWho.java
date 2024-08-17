package me.hapyl.fight.guesswho;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Lifecycle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GuessWho extends GameTask implements Lifecycle {

    private static final int BOARD_SIZE = 28;

    public final GuessWhoPlayer player1;
    public final GuessWhoPlayer player2;

    private final List<Hero> board;
    public GameResult result;
    private PlayerType turn;
    private int round;
    private GameState state;

    public GuessWho(Player player1, Player player2) {
        this.player1 = new GuessWhoPlayer(this, player1, PlayerType.FIRST);
        this.player2 = new GuessWhoPlayer(this, player2, PlayerType.SECOND);

        this.turn = PlayerType.FIRST;
        this.state = GameState.PREPARING;
        this.round = 1;

        this.board = new ArrayList<>(BOARD_SIZE);

        runTaskTimer(0, 20);
    }

    @Override
    public void onStart() {
        // Teleport players

        generateBoard();
    }

    @Override
    public void onStop() {
        cancel();
    }

    @Nonnull
    public PlayerType getTurn() {
        return turn;
    }

    @Nonnull
    public List<Hero> getBoard() {
        return board;
    }

    public void checkSelected() {
        if (!player1.hasGuessHero()) {
            return;
        }

        if (!player2.hasGuessHero()) {
            return;
        }

        setState(GameState.IN_GAME);
    }

    public void sendMessage(@Nonnull String message) {
        asBothPlayers(player -> player.sendMessage(message));
    }

    public void promptPlayers() {
        GameTask.runLater(() -> asBothPlayers(GuessWhoPlayer::promptSelectHero), 2);
    }

    public void nextTurn() {
        turn = turn == PlayerType.FIRST ? PlayerType.SECOND : PlayerType.FIRST;

        if (turn == PlayerType.FIRST) {
            round++;
        }

        // Open GUI for both players because
        promptPlayers();
    }

    @Override
    public void run() {
        asBothPlayers(player -> {
            final Player bukkitPlayer = player.getPlayer();
            final GuessWhoPlayer opponent = player.getOpponent();
            final String opponentName = opponent.getPlayer().getName();

            final InventoryView openInventory = bukkitPlayer.getOpenInventory();

            if (!openInventory.getTitle().contains("Guess Who")) {
                Chat.sendActionbar(bukkitPlayer, "&aUse &e/guesswho&a to open the GUI!");
            }

            if (state != GameState.IN_GAME) {
                if (player.hasGuessHero()) {
                    player.sendTitle("&6sᴇʟᴇᴄᴛᴇᴅ", "Waiting on your opponent...", 0, 25, 0);
                }
                else {
                    player.sendTitle("&6sᴇʟᴇᴄᴛ ʏᴏᴜʀ ʜᴇʀᴏ", "Your opponent will have to guess it!", 0, 25, 0);
                }

                return;
            }

            if (!player.isMyTurn()) {
                player.sendTitle(
                        "&bᴡᴀɪᴛɪɴɢ",
                        "It's %s's turn, answer their question!".formatted(opponentName),
                        0, 25, 0
                );
            }
            else {
                player.sendTitle(
                        "&aʏᴏᴜʀ ᴛᴜʀɴ",
                        "Ask %s a &ayes&f or &cno&f question!".formatted(opponentName),
                        0, 25, 0
                );
            }
        });
    }

    @Nonnull
    public GameState getState() {
        return state;
    }

    public void setState(@Nonnull GameState state) {
        this.state = state;
        this.state.onStart(this);
    }

    @Nonnull
    public GuessWhoPlayer getWinner() {
        if (player1.isWinner()) {
            return player1;
        }
        else if (player2.isWinner()) {
            return player2;
        }

        throw new IllegalStateException("No winners yet.");
    }

    @Nonnull
    public GuessWhoPlayer getLoser() {
        final GuessWhoPlayer winner = getWinner();

        return player1 == winner ? player2 : player1;
    }

    public void loseBecauseLeft(Player player) {
        final GuessWhoPlayer gamePlayer = getPlayer(player);

        result = GameResult.LEFT;

        if (gamePlayer != null) {
            gamePlayer.triggerLose();
        }
    }

    public int getCurrentRound() {
        return round;
    }

    @Nullable
    public GuessWhoPlayer getPlayer(Player player) {
        return player1.getPlayer() == player ? player1 : player2.getPlayer() == player ? player2 : null;
    }

    protected void asBothPlayers(Consumer<GuessWhoPlayer> consumer) {
        consumer.accept(player1);
        consumer.accept(player2);
    }

    private void generateBoard() {
        final List<Hero> heroes = HeroRegistry.playable();

        Collections.shuffle(heroes);
        heroes.stream().limit(BOARD_SIZE).forEach(this.board::add);

        this.state = GameState.HERO_SELECTION;

        asBothPlayers(player -> {
            player.setBoard();
            player.promptSelectHero();
        });
    }

}
