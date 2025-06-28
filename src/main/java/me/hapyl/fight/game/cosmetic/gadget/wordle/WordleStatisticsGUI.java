package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.WordleEntry;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.util.ThreadOps;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordleStatisticsGUI extends StyledGUI {
    
    private transient List<WordleResult> results;
    
    WordleStatisticsGUI(@Nonnull Player player) {
        super(player, "Wordle Statistics", Size.FIVE);
        
        final WordleEntry entry = CF.getDatabase(player).wordleEntry;
        
        ThreadOps.async(() -> {
            // Load results async
            results = entry.results();
            
            // Reopen the inventory
            ThreadOps.sync(this::openInventory);
        });
        
        openInventory();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        setHeader(StyledTexture.ICON_WORDLE_STATISTICS.asIcon());
        
        // Still loading
        if (results == null) {
            setItem(
                    22, StyledTexture.LOADING.asBuilder()
                                             .addTextBlockLore("""
                                                               Statistics are still being loaded, please wait!
                                                               """)
                                             .asIcon()
            );
            return;
        }
        
        setItem(22, makeGeneralStatsItem());
        
        // Display last 7 games
        int absoluteIndex = 0;
        
        for (int index = Math.max(0, results.size() - 7); index < results.size(); index++) {
            final WordleResult result = results.get(index);
            final int slot = 28 + absoluteIndex++;
            final WordleWord hiddenWord = Wordle.byIndex(result.index());
            
            if (hiddenWord == null) {
                setItem(
                        slot, new ItemBuilder(Material.BARRIER)
                                .setName("&cError!")
                                .addTextBlockLore("""
                                                  An error has occurred while loading this, try again because reporting this!
                                                  """)
                                .asIcon()
                );
                continue;
            }
            
            final ItemBuilder builder = new ItemBuilder(Material.MAP)
                    .setName("Daily Wordle #%s".formatted(result.index() + 1))
                    .setAmount(absoluteIndex + 1);
            
            builder.addLore()
                   .addLore("&f&l%s".formatted(result.hiddenWord().toUpperCase()))
                   .addLore(" &7&o%s".formatted(hiddenWord.definition()))
                   .addLore();
            
            for (String guess : result.guesses()) {
                builder.addLore("  " + new WordleGuess(guess, hiddenWord).toStringAsCharsInBoxes());
            }
            
            setItem(slot, builder.asIcon());
        }
    }
    
    private ItemStack makeGeneralStatsItem() {
        final int played = results.size();
        final double winRate = played == 0
                               ? 0
                               : (double) results.stream().filter(WordleResult::isWin).count() / played;
        
        // Calculate current win streaks
        final int currentWinStreak = calculateWinStreak();
        final int maxWinStreak = calculateMaxWinStreak();
        
        // Make builder
        final ItemBuilder builder = new ItemBuilder(Material.FLOW_BANNER_PATTERN);
        builder.setName("General Statistics");
        builder.addTextBlockLore("""
                                 
                                 Played: &b%s
                                 Win Rate: &a%.0f%%
                                 
                                 Current Streak: &6%s
                                 Max Streak: &6%s
                                 """.formatted(played, winRate * 100, currentWinStreak, maxWinStreak));
        
        // Append guess distribution
        builder.addLore();
        builder.addLore("Guess Distributions:");
        
        final Distribution[] distributions = calculateGuessStat();
        final int barLength = 50;
        
        for (Distribution distribution : distributions) {
            final double percent = distribution.percent;
            final int bars = (int) (percent * barLength);
            
            builder.addLore(" &8%s. %s &f&l%s".formatted(
                    distribution.guessCount,
                    "&a|".repeat(bars) + "&8|".repeat(barLength - bars),
                    distribution.wins
            ));
        }
        
        return builder.asIcon();
    }
    
    private int calculateWinStreak() {
        int winStreak = 0;
        
        for (int i = results.size() - 1; i >= 0; i--) {
            if (!results.get(i).isWin()) {
                break;
            }
            
            winStreak++;
        }
        
        return winStreak;
    }
    
    private int calculateMaxWinStreak() {
        int maxWinStreak = 0;
        int streak = 0;
        
        for (WordleResult result : results) {
            if (result.isWin()) {
                streak++;
                maxWinStreak = Math.max(maxWinStreak, streak);
            }
            else {
                streak = 0;
            }
        }
        
        return maxWinStreak;
    }
    
    private Distribution[] calculateGuessStat() {
        final Map<Integer, Integer> winsByGuesses = new HashMap<>();
        
        int totalWins = 0;
        for (WordleResult result : results) {
            if (!result.isWin()) {
                continue;
            }
            
            final int guessCount = result.guesses().size();
            
            if (guessCount >= 1 && guessCount <= 6) {
                winsByGuesses.merge(guessCount, 1, Integer::sum);
                totalWins++;
            }
        }
        
        final Distribution[] distribution = new Distribution[6];
        
        for (int i = 1; i <= 6; i++) {
            final int count = winsByGuesses.getOrDefault(i, 0);
            final double winPercent = totalWins == 0 ? 0 : (double) count / totalWins;
            
            distribution[i - 1] = new Distribution(i, count, winPercent);
        }
        
        return distribution;
    }
    
    private record Distribution(int guessCount, int wins, double percent) {
    }
    
}
