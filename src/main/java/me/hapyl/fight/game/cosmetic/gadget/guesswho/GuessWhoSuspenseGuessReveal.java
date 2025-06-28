package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.heroes.Hero;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class GuessWhoSuspenseGuessReveal implements Ticking {
    
    private static final Integer[] delays = {
            5, 6, 8, 11, 15, 20, 26, 33, 41, 50,
            60, 71, 83, 96, 110, 125, 141, 158, 176, 195,
            215, 236, 258, 281, 305, 330, 356, 383
    };
    
    private final GuessWhoPlayer guesser;
    private final LinkedList<Hero> revealOrder;
    
    private final Hero guess;
    private final Hero opponentHero;
    
    private final GuessWhoGuessGUI[] revealGUIs;
    private int tick;
    
    GuessWhoSuspenseGuessReveal(@Nonnull GuessWhoPlayer player, @Nonnull Hero guess) {
        this.guesser = player;
        this.revealOrder = new LinkedList<>(player.getGame().board());
        this.guess = guess;
        
        final GuessWhoPlayer opponent = player.opponent();
        this.opponentHero = opponent.selectedHero();
        
        final Hero lastGuess;
        final Hero secondToLastGuess;
        
        // If the player has guessed the correct hero, lastGuess is the guess
        // and secondToLastGuess is a random from the board
        if (guess == opponentHero) {
            lastGuess = guess;
            secondToLastGuess = CollectionUtils.randomElementOrFirst(revealOrder.stream().filter(hero -> hero != guess).toList());
        }
        // Otherwise, the lastGuess is the opponentHero and the secondToLastGuess it's guess
        else {
            lastGuess = opponentHero;
            secondToLastGuess = guess;
        }
        
        // Remove both guesses from the revealOrder
        revealOrder.removeAll(List.of(lastGuess, secondToLastGuess));
        
        // Shuffle the reveal order
        Collections.shuffle(revealOrder);
        
        // Put the last two guesses back
        revealOrder.add(secondToLastGuess);
        revealOrder.add(lastGuess);
        
        // Make GUIs
        revealGUIs = new GuessWhoGuessGUI[] { new GuessWhoGuessGUI(player, this), new GuessWhoGuessGUI(opponent, this) };
        
        callGUIMethod(PlayerGUI::openInventory);
    }
    
    @Nonnull
    public GuessWhoPlayer guesser() {
        return guesser;
    }
    
    public boolean isCorrectGuess() {
        return guess == opponentHero;
    }
    
    public boolean contains(@Nonnull Hero hero) {
        return revealOrder.contains(hero);
    }
    
    @Override
    public void tick() {
        final GuessWhoActivity instance = guesser.getGame();
        
        final int size = revealOrder.size();
        final int delay = delays[delays.length - size];
        final double normalized = size * delays.length;
        
        if (tick++ < delay) {
            return;
        }
        
        revealOrder.pollFirst();
        
        callGUIMethod(PlayerGUI::openInventory);
        
        // Don't remove last hero, reveal it and trigger win/lose
        if (revealOrder.size() == 1) {
            if (isCorrectGuess()) {
                instance.win(guesser, GameResult.GUESSED_CORRECTLY);
            }
            else {
                instance.win(guesser.opponent(), GameResult.GUESSED_INCORRECTLY);
            }
        }
        
        // Fx
        instance.asBothPlayers(player -> player.playSound(Sound.BLOCK_LAVA_POP, (float) (1.5 - normalized)));
    }
    
    public boolean empty() {
        return revealOrder.size() == 1;
    }
    
    private void callGUIMethod(Consumer<PlayerGUI> method) {
        for (GuessWhoGuessGUI gui : revealGUIs) {
            method.accept(gui);
        }
    }
}
