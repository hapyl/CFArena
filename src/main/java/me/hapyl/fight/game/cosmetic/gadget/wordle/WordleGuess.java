package me.hapyl.fight.game.cosmetic.gadget.wordle;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WordleGuess {
    
    private static final Map<Character, String> squaredLetters = Map.ofEntries(
            Map.entry('a', "🄰"),
            Map.entry('b', "🄱"),
            Map.entry('c', "🄲"),
            Map.entry('d', "🄳"),
            Map.entry('e', "🄴"),
            Map.entry('f', "🄵"),
            Map.entry('g', "🄶"),
            Map.entry('h', "🄷"),
            Map.entry('i', "🄸"),
            Map.entry('j', "🄹"),
            Map.entry('k', "🄺"),
            Map.entry('l', "🄻"),
            Map.entry('m', "🄼"),
            Map.entry('n', "🄽"),
            Map.entry('o', "🄾"),
            Map.entry('p', "🄿"),
            Map.entry('q', "🅀"),
            Map.entry('r', "🅁"),
            Map.entry('s', "🅂"),
            Map.entry('t', "🅃"),
            Map.entry('u', "🅄"),
            Map.entry('v', "🅅"),
            Map.entry('w', "🅆"),
            Map.entry('x', "🅇"),
            Map.entry('y', "🅈"),
            Map.entry('z', "🅉")
    );
    
    protected final String guess;
    protected final Entry[] charValue;
    
    public WordleGuess(@Nonnull String guess, @Nonnull WordleWord hiddenWord) {
        this.guess = guess.toLowerCase();
        this.charValue = new Entry[Wordle.WORD_LENGTH];
        
        // Calculate relations
        final String word = hiddenWord.word();
        final Map<Character, Integer> charCount = hiddenWord.charCount();
        
        // First pass, mark green letters
        for (int i = 0; i < guess.length(); i++) {
            final char guessChar = guess.charAt(i);
            final char wordChar = word.charAt(i);
            
            if (guessChar == wordChar) {
                charValue[i] = new Entry(guessChar, CharacterValue.CORRECT);
                charCount.compute(guessChar, (c, ci) -> Objects.requireNonNull(ci) - 1);
            }
        }
        
        for (int i = 0; i < guess.length(); i++) {
            // If already defined, skip
            if (charValue[i] != null) {
                continue;
            }
            
            final char guessChar = guess.charAt(i);
            final int count = charCount.getOrDefault(guessChar, 0);
            
            if (count > 0) {
                charValue[i] = new Entry(guessChar, CharacterValue.WRONG_POSITION);
                charCount.compute(guessChar, (c, ci) -> Objects.requireNonNull(ci) - 1);
            }
            else {
                charValue[i] = new Entry(guessChar, CharacterValue.INCORRECT);
            }
        }
    }
    
    @Nonnull
    public String guess() {
        return guess;
    }
    
    @Nullable
    public CharacterValue charValue(char c) {
        return Arrays.stream(charValue)
                     .filter(entry -> entry.c == c)
                     .findFirst()
                     .map(entry -> entry.value)
                     .orElse(null);
    }
    
    @Override
    public String toString() {
        return Arrays.stream(charValue)
                     .map(entry -> entry.value.toString())
                     .collect(Collectors.joining(" "));
    }
    
    @Nonnull
    public String toStringAsCharsInBoxes() {
        return Arrays.stream(charValue)
                     .map(entry -> entry.value.color() + squaredLetters.getOrDefault(Character.toLowerCase(entry.c), "?"))
                     .collect(Collectors.joining(" "));
    }
    
    @Nonnull
    public ItemStack[] charsAsItems() {
        return Arrays.stream(charValue)
                     .map(entry -> WordleChar.byChar(entry.c).letterOf(entry.value))
                     .toArray(ItemStack[]::new);
    }
    
    public record Entry(char c, @Nonnull CharacterValue value) {
    }
    
}
