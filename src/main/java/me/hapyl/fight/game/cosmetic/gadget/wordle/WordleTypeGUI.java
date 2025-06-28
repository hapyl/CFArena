package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.StrictAction;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.WordleEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WordleTypeGUI extends StyledGUI {
    
    private final PlayerDatabase database;
    
    public WordleTypeGUI(@Nonnull Player player) {
        super(player, "Wordle", Size.FOUR);
        
        this.database = CF.getDatabase(player);
        
        openInventory();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        setHeader(Registries.cosmetics().WORDLE.createItem(player).removeLore().asIcon());
        
        final WordleEntry wordleEntry = database.wordleEntry;
        
        final int todayIndex = Wordle.todayIndex();
        final WordleWord todayWord = Wordle.todayWord();
        
        // Daily
        final ItemBuilder builder
                = ItemBuilder.playerHeadUrl("25485031b37f0d8a4f3b7816eb717f03de89a87f6a40602aef52221cdfaf7488")
                             .setName("Daily Worlde")
                             .addLore("&8#" + (todayIndex + 1))
                             .addTextBlockLore("""
                                               Guess a mystery 5-letter word on a daily rotation and win &6rewards&7!
                                               """)
                             .addLore();
        
        @Nullable final WordleResult todayResult = wordleEntry.result(todayIndex);
        
        if (todayResult != null) {
            builder.addTextBlockLore("""
                                     Today's Result:
                                     
                                     """);
            
            for (String guess : todayResult.guesses()) {
                builder.addLore("  " + new WordleGuess(guess, todayWord).toStringAsCharsInBoxes());
            }
            
            builder.addLore();
            builder.addLore(Color.SUCCESS + "Come back tomorrow!");
        }
        else {
            builder.addLore(Color.BUTTON + "Click to start guessing!");
        }
        
        setItem(
                20, builder.asIcon(), new StrictAction() {
                    @Override
                    public void onLeftClick(@Nonnull Player player) {
                        if (todayResult == null) {
                            new WordleGUI(wordleEntry.today());
                            return;
                        }
                        
                        Message.error(player, "You have already complete today's puzzle!");
                        Message.sound(player, SoundEffect.ERROR);
                    }
                }
        );
        
        // Random
        setItem(
                24, ItemBuilder.playerHeadUrl("f340d50d7d1293ba16d23c6d07ab066cdc1575c68bca69e96f0bb6d1ce1bf1ba")
                               .setName("Random Wordle")
                               .addTextBlockLore
                                       ("""
                                        &8Random
                                        
                                        Already guessed the daily puzzle, or just want to play more? No problem, try guessing a random wordle!
                                        
                                        &c&o;;Random wordle do not give reward nor track statistics.
                                        
                                        %sClick to guess a random wordle!
                                        """.formatted(Color.BUTTON))
                               .asIcon(), player -> new WordleGUI(new WordlePracticeInstance(player))
        );
        
        // Statistics
        setItem(31, StyledTexture.ICON_WORDLE_STATISTICS.asButton("view"), WordleStatisticsGUI::new);
    }
    
}
