package me.hapyl.fight.game.cosmetic.gadget.wordle;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Collectors;

public class WordleWord {
    
    private final int index;
    private final String word;
    private final String definition;
    
    public WordleWord(int index, @Nonnull String word, @Nonnull String definition) {
        this.index = index;
        this.word = word;
        this.definition = definition;
    }
    
    public int index() {
        return index;
    }
    
    @Nonnull
    public String word() {
        return word;
    }
    
    @Nonnull
    public String definition() {
        return definition;
    }
    
    @Nonnull
    public Map<Character, Integer> charCount() {
        return word.chars()
                   .mapToObj(c -> (char) c)
                   .collect(Collectors.toMap(
                           c -> c,
                           c -> 1,
                           Integer::sum
                   ));
    }
    
    @Override
    public String toString() {
        return "%s: %s".formatted(index, word);
    }
}
