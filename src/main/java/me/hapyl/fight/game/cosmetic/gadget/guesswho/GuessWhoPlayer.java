package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.GuessWhoEntry;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GuessWhoPlayer {
    
    private final GuessWhoActivity game;
    private final Player player;
    private final boolean isFirstPlayer;
    private final Set<Hero> board;
    
    protected boolean winner;
    private Hero selectedHero;
    
    GuessWhoPlayer(GuessWhoActivity game, Player player, List<Hero> board, boolean isFirstPlayer) {
        this.game = game;
        this.player = player;
        this.isFirstPlayer = isFirstPlayer;
        this.board = Sets.newHashSet(board);
    }
    
    public boolean hasSelectedHero() {
        return selectedHero != null;
    }
    
    public boolean isWinner() {
        return winner;
    }
    
    @Nonnull
    public Hero selectedHero() {
        return Objects.requireNonNull(selectedHero, "Illegal state");
    }
    
    public void selectedHero(@Nonnull Hero hero) {
        if (selectedHero != null) {
            return;
        }
        
        this.selectedHero = hero;
    }
    
    @Nonnull
    public String getGuessHeroName() {
        return selectedHero != null ? selectedHero.getNameSmallCaps() : "Unknown";
    }
    
    public boolean isMyTurn() {
        return game.turn() == isFirstPlayer;
    }
    
    public void promptGUI() {
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
    
    @Nonnull
    public GuessWhoActivity getGame() {
        return game;
    }
    
    public boolean hasRuledOut(@Nonnull Hero enumHero) {
        return !board.contains(enumHero);
    }
    
    public void sendMessage(@Nonnull String message) {
        Chat.sendMessage(player, "&a&lɢᴜᴇss ᴡʜᴏ&2?¿&f " + message);
    }
    
    @Nonnull
    public GuessWhoPlayer opponent() {
        return game.player1 == this ? game.player2 : game.player1;
    }
    
    public void ruleOut(@Nonnull Set<Hero> toRuleOut) {
        board.removeAll(toRuleOut);
        
        // Fx
        sendMessage("Ruled out " + Chat.makeStringCommaAnd(toRuleOut, Hero::getNameSmallCaps) + "!");
        playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
    }
    
    public void guess(@Nonnull Hero hero) {
        if (game.suspense() != null) {
            sendMessage("&4Already revealing!");
            return;
        }
        
        game.suspense(new GuessWhoSuspenseGuessReveal(this, hero));
    }
    
    @Override
    public String toString() {
        final PlayerProfile profile = CF.getProfile(player);
        
        return profile.display().toString();
    }
    
    @Nonnull
    public String getName() {
        return player.getName();
    }
    
    public int getBoardSize() {
        return board.size();
    }
    
    public GuessWhoEntry getEntry() {
        return CF.getDatabase(player).guessWhoEntry;
    }
    
    public boolean isGUIOpen() {
        return PlayerGUI.getPlayerGUI(player) instanceof GuessWhoGUI;
    }
    
    public void showHowToOpenGUI() {
        Chat.sendActionbar(player, "&fUse &e/guesswho&f to open the GUI!");
    }
    
    // I/O helpers
    public void playSound(@Nonnull Sound sound, float pitch) {
        PlayerLib.playSound(player, sound, pitch);
    }
    
    public void title(@Nonnull String title, @Nonnull String subTitle) {
        title(title, subTitle, 0, 25, 0);
    }
    
    public void title(@Nonnull String title, @Nonnull String subTitle, int in, int stay, int out) {
        Chat.sendTitle(player, title, subTitle, in, stay, out);
    }
    
    protected void onWin() {
        final GuessWhoEntry entry = getEntry();
        
        entry.incrementStat(GuessWhoEntry.StatType.PLAYED);
        entry.incrementStat(GuessWhoEntry.StatType.WINS);
        entry.incrementStat(GuessWhoEntry.StatType.WIN_STREAK);
        
        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
    }
    
    protected void onLose() {
        final GuessWhoEntry entry = getEntry();
        
        entry.incrementStat(GuessWhoEntry.StatType.PLAYED);
        entry.incrementStat(GuessWhoEntry.StatType.LOSES);
        entry.resetStat(GuessWhoEntry.StatType.WIN_STREAK);
        
        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_CAT_AMBIENT, 0.5f);
    }
    
}
