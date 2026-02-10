package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.activity.Activity;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GuessWhoActivity implements Activity {
    
    private static final int BOARD_SIZE = 28;
    
    public final GuessWhoPlayer player1;
    public final GuessWhoPlayer player2;
    
    private final List<Hero> board;
    
    private GameState state;
    
    @Nullable private GameResult result;
    @Nullable private GuessWhoSuspenseGuessReveal suspense;
    
    private boolean turn;
    private int round;
    
    public GuessWhoActivity(@Nonnull Player player1, @Nonnull Player player2) {
        final boolean firstPlayerStartsFirst = BukkitUtils.RANDOM.nextBoolean();
        
        // Generate board before players because we need it in GuessWhoPlayer
        this.board = generateBoard();
        this.state = GameState.HERO_SELECTION;
        
        this.player1 = new GuessWhoPlayer(this, player1, board, firstPlayerStartsFirst);
        this.player2 = new GuessWhoPlayer(this, player2, board, !firstPlayerStartsFirst);
        
        this.turn = true;
        this.round = 1;
    }
    
    @Override
    public void onStart(@Nonnull Player player) {
        this.state.onStart(this);
    }
    
    @Override
    public void onStop(@Nonnull Player player) {
    }
    
    public int round() {
        return round;
    }
    
    @Override
    public void onTick(@Nonnull Player player, int tick) {
        final GuessWhoPlayer guessWhoPlayer = getPlayer(player);
        final GuessWhoPlayer opponent = guessWhoPlayer.opponent();
        
        if (tick % 20 == 0) {
            // Display how to open the GUI is not opened
            if (!guessWhoPlayer.isGUIOpen()) {
                guessWhoPlayer.showHowToOpenGUI();
            }
            
            // Display title based on the state
            switch (state) {
                case HERO_SELECTION -> {
                    if (guessWhoPlayer.hasSelectedHero()) {
                        guessWhoPlayer.title("&6sᴇʟᴇᴄᴛᴇᴅ", "&eWaiting on your opponent...");
                    }
                    else {
                        guessWhoPlayer.title("&6sᴇʟᴇᴄᴛ ʏᴏᴜʀ ʜᴇʀᴏ", "&eYour opponent will have to guess it!");
                    }
                }
                case IN_GAME -> {
                    if (guessWhoPlayer.isMyTurn()) {
                        guessWhoPlayer.title("&aʏᴏᴜʀ ᴛᴜʀɴ", "Ask %s a &ayes&f or &cno&f question!".formatted(opponent.getName()));
                    }
                    else {
                        guessWhoPlayer.title("&bᴡᴀɪᴛɪɴɢ", "It's %s's turn, answer their question!".formatted(opponent.getName()));
                    }
                }
            }
        }
        
        // Tick suspense
        if (suspense != null) {
            suspense.tick();
        }
    }
    
    @Override
    public void onKick(@Nonnull Player player) {
        win(getPlayer(player).opponent(), GameResult.LEFT);
    }
    
    @Nonnull
    @Override
    public List<Player> players() {
        return List.of(player1.getPlayer(), player2.getPlayer());
    }
    
    @Nullable
    public GuessWhoSuspenseGuessReveal suspense() {
        return suspense;
    }
    
    public void suspense(@Nonnull GuessWhoSuspenseGuessReveal suspense) {
        this.state = GameState.REVEALING_GUESS;
        this.suspense = suspense;
    }
    
    @Nonnull
    public GameResult result() {
        return Objects.requireNonNull(result, "Illegal state");
    }
    
    public boolean turn() {
        return turn;
    }
    
    @Nonnull
    public List<Hero> board() {
        return board;
    }
    
    public void checkSelected() {
        if (!player1.hasSelectedHero()) {
            return;
        }
        
        if (!player2.hasSelectedHero()) {
            return;
        }
        
        setState(GameState.IN_GAME);
    }
    
    public void sendMessage(@Nonnull String message) {
        asBothPlayers(player -> player.sendMessage(message));
    }
    
    public void promptPlayers() {
        GameTask.runLater(() -> asBothPlayers(GuessWhoPlayer::promptGUI), 2);
    }
    
    public void nextTurn() {
        turn = !turn;
        
        if (turn) {
            round++;
        }
        
        // Open GUI for both players because
        promptPlayers();
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
    
    @Nonnull
    public GuessWhoPlayer getPlayer(@Nonnull Player player) {
        if (player1.getPlayer().equals(player)) {
            return player1;
        }
        else if (player2.getPlayer().equals(player)) {
            return player2;
        }
        
        throw new IllegalArgumentException("Non-instanced player: " + player);
    }
    
    public void win(@Nonnull GuessWhoPlayer guesser, @Nonnull GameResult result) {
        guesser.winner = true;
        guesser.onWin();
        
        final GuessWhoPlayer opponent = guesser.opponent();
        
        opponent.winner = false;
        opponent.onLose();
        
        this.result = result; // Make sure to set result BEFORE state
        setState(GameState.POST_GAME); // POST_GAME handles profile activity
    }
    
    protected void asBothPlayers(Consumer<GuessWhoPlayer> consumer) {
        consumer.accept(player1);
        consumer.accept(player2);
    }
    
    private static List<Hero> generateBoard() {
        final List<Hero> heroes = HeroRegistry.playable();
        
        Collections.shuffle(heroes);
        return heroes.stream().limit(BOARD_SIZE).toList();
    }
    
}
