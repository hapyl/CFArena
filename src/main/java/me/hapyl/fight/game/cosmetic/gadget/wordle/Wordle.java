package me.hapyl.fight.game.cosmetic.gadget.wordle;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.CF;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class Wordle {
    
    public static final int WORD_LENGTH = 5;
    public static final int MAX_GUESSSES = 6;
    
    private static final List<WordleWord> dictionary;
    
    static {
        dictionary = Lists.newArrayList();
        
        // Parse the yml file async
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final InputStream resource = Objects.requireNonNull(CF.getPlugin().getResource("wordle.yml"), "Missing 'wordle.yml' in /resources!");
                    final YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
                    final ConfigurationSection words = Objects.requireNonNull(yml.getConfigurationSection("words"), "Malformed 'wordle.yml'!");
                    
                    int index = 0;
                    for (String word : words.getKeys(false)) {
                        // Definitions are done using AI, so make sure I removed all hallucinations
                        if (word.length() != WORD_LENGTH) {
                            throw new IllegalArgumentException("Word length must be %s, %s is %s!".formatted(WORD_LENGTH, word, word.length()));
                        }
                        
                        final String definition = Objects.requireNonNull(words.getString(word), "Missing definition for '%s'!".formatted(word));
                        
                        dictionary.add(new WordleWord(index++, word, definition));
                    }
                }
                catch (Exception e) {
                    dictionary.clear();
                    throw new RuntimeException("Failed to parsed 'wordle.yml'! %s".formatted(e.getMessage()), e);
                }
            }
        }.runTaskAsynchronously(CF.getPlugin());
        
    }
    
    private Wordle() {
    }
    
    @Nonnull
    public static List<WordleWord> subdict(int max) {
        return subdict(0, max);
    }
    
    @Nonnull
    public static List<WordleWord> subdict(int min, int max) {
        if (min > max) {
            return List.of();
        }
        
        return dictionary.subList(
                Math.max(0, min - 1),
                Math.min(dictionary.size(), max)
        );
    }
    
    @Nullable
    public static WordleWord byIndex(int index) {
        return index < 0 || index >= dictionary.size() ? null : dictionary.get(index);
    }
    
    @Nonnull
    public static WordleWord random() {
        return Objects.requireNonNull(CollectionUtils.randomElement(dictionary), "Calling random() before load");
    }
    
    public static boolean isValidWord(@Nonnull String string) {
        return dictionary.stream().anyMatch(word -> word.word().equalsIgnoreCase(string));
    }
    
    private static final LocalDate wordleStart = LocalDate.of(2025, 6, 1);
    
    public static int todayIndex() throws IllegalArgumentException {
        final LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        final long days = ChronoUnit.DAYS.between(wordleStart, today);
        
        if (days < 0 || days >= dictionary.size()) {
            throw new IllegalArgumentException("Illegal day: " + days);
        }
        
        return (int) days;
    }
    
    @Nonnull
    public static WordleWord todayWord() {
        return dictionary.get(todayIndex());
    }
    
    @Nullable
    public static WordleWord indexOf(String string) {
        return dictionary.stream()
                         .filter(word -> word.word().equalsIgnoreCase(string))
                         .findFirst()
                         .orElse(null);
    }
}
