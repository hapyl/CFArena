package me.hapyl.fight.guesswho;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.GuessWhoEntry;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.guesswho.gui.GuessWhoRuleOutGUI;
import me.hapyl.fight.guesswho.gui.GuessWhoSelectionGUI;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class GuessWhoPlayer {

    private final GuessWho game;
    private final Player player;
    private final PlayerType type;
    private final List<Heroes> board;

    private Heroes guessHero;
    private boolean winner;

    public GuessWhoPlayer(GuessWho game, Player player, PlayerType type) {
        this.game = game;
        this.player = player;
        this.type = type;
        this.board = Lists.newArrayList();
    }

    public boolean hasGuessHero() {
        return guessHero != null;
    }

    public boolean isWinner() {
        return winner;
    }

    @Nullable
    public Heroes getGuessHero() {
        return guessHero;
    }

    public void setGuessHero(@Nonnull Heroes clicked) {
        if (guessHero != null) {
            return;
        }

        this.guessHero = clicked;
    }

    @Nonnull
    public String getGuessHeroName() {
        return guessHero != null ? guessHero.getNameSmallCaps() : "Unknown";
    }

    public boolean isMyTurn() {
        return game.getTurn() == type;
    }

    public void setBoard() {
        if (!board.isEmpty()) {
            throw new IllegalStateException("Board already set.");
        }

        board.addAll(game.getBoard());
    }

    public void promptSelectHero() {
        final GameState state = game.getState();

        switch (state) {
            case HERO_SELECTION -> new GuessWhoSelectionGUI(this);
            case IN_GAME -> new GuessWhoRuleOutGUI(this);
        }

        // Fx
        playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Heroes getSelectedHero() {
        return guessHero;
    }

    @Nonnull
    public GuessWho getGame() {
        return game;
    }

    public boolean isRuledOut(@Nonnull Heroes enumHero) {
        return !board.contains(enumHero);
    }

    public void sendMessage(@Nonnull String message) {
        Chat.sendMessage(player, "&2? &a&lɢᴜᴇss ᴡʜᴏ &2¿ &f" + message);
    }

    public void playSound(@Nonnull Sound sound, float pitch) {
        PlayerLib.playSound(player, sound, pitch);
    }

    public void sendTitle(@Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut) {
        Chat.sendTitle(player, title, subTitle, fadeIn, stay, fadeOut);
    }

    @Nonnull
    public GuessWhoPlayer getOpponent() {
        return game.player1 == this ? game.player2 : game.player1;
    }

    public boolean isLastHero() {
        return board.size() == 1;
    }

    public void ruleOut(@Nonnull Set<Heroes> toRuleOut) {
        board.removeAll(toRuleOut);

        // Fx
        sendMessage("Ruled out " + Chat.makeStringCommaAnd(toRuleOut, Heroes::getNameSmallCaps) + "!");
        playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
    }

    public void guess(@Nonnull Heroes hero) {
        final GuessWhoPlayer opponent = getOpponent();

        // Fx
        sendMessage("Guessing %s...".formatted(hero.getName()));

        if (opponent.guessHero == hero) {
            game.result = GameResult.GUESSED_CORRECTLY;
            triggerWin();
        }
        else {
            game.result = GameResult.GUESSED_INCORRECTLY;
            triggerLose();
        }
    }

    public void lose() {
        winner = false;
        final GuessWhoEntry entry = getEntry();

        entry.incrementStat(GuessWhoEntry.StatType.LOSES);
        entry.resetStat(GuessWhoEntry.StatType.WIN_STREAK);
    }

    public void win() {
        this.winner = true;

        final GuessWhoEntry entry = getEntry();

        entry.incrementStat(GuessWhoEntry.StatType.WINS);
        entry.incrementStat(GuessWhoEntry.StatType.WIN_STREAK);

        game.setState(GameState.POST_GAME);
    }

    @Override
    public String toString() {
        return player.getName();
    }

    @Nonnull
    public String getProfileName() {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return toString();
        }

        return profile.getDisplay().getNamePrefixed();
    }

    @Nonnull
    public String getName() {
        return player.getName();
    }

    public int getBoardSize() {
        return board.size();
    }

    public GuessWhoEntry getEntry() {
        return PlayerDatabase.getDatabase(player).guessWhoEntry;
    }

    public void triggerLose() {
        this.lose();
        getOpponent().win();
    }

    public void triggerWin() {
        this.win();
        getOpponent().lose();
    }
}
