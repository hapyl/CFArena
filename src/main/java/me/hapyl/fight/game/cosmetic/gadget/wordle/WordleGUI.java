package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.Response;
import me.hapyl.eterna.module.inventory.SignGUI;
import me.hapyl.eterna.module.inventory.gui.GUIEventListener;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class WordleGUI extends StyledGUI implements GUIEventListener {
    
    private static final int singSlot = 36;
    private static final char[] qwerty = "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
    
    private static final ItemStack columnItemWin = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName("").asIcon();
    private static final ItemStack columnItemLose = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("").asIcon();
    
    private static final String textureHardModeEnabled = "cb852ba1584da9e5714859995451e4b94748c4dd63ae4543c15f9f8aec65c8";
    private static final String textureHardModeDisabled = "1ae3855f952cd4a03c148a946e3f812a5955ad35cbcb52627ea4acd47d3081";
    
    private final WordleInstance instance;
    private WorldeState state;
    
    public WordleGUI(@Nonnull WordleInstance instance) {
        super(instance.player(), "Wordle #%s (%s)".formatted(instance.hiddenWord().index() + 1, instance.type()), Size.NOT_STYLED);
        
        this.instance = instance;
        this.state = WorldeState.GUESSING;
        
        openInventory();
    }
    
    @Override
    public void onClose(@Nonnull InventoryCloseEvent event) {
        // Unless guessing assume lost
        if (state == WorldeState.AWAITING_GUESS || state == WorldeState.GAME_OVER) {
            return;
        }
        
        instance.onQuit();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        state = WorldeState.GUESSING;
        
        // Fill words
        final List<WordleGuess> guesses = instance.guesses();
        
        // Fill columns
        columns(ItemStacks.BLACK_BAR);
        
        for (int i = 0; i < guesses.size(); i++) {
            final WordleGuess guess = guesses.get(i);
            final ItemStack[] charsAsItems = guess.charsAsItems();
            
            for (int j = 0; j < charsAsItems.length; j++) {
                setItem(2 + i * 9 + j, charsAsItems[j]);
            }
        }
        
        // Set guess button
        setItem(
                singSlot, createSignItem(), player -> {
                    state = WorldeState.AWAITING_GUESS;
                    
                    new SignGUI(player, "Enter Five Letter Word") {
                        @Override
                        public void onResponse(Response response) {
                            final String word = response.getString(0).trim();
                            
                            if (word.length() != Wordle.WORD_LENGTH) {
                                error("A word must be five letters long!");
                                return;
                            }
                            
                            if (!Wordle.isValidWord(word)) {
                                error("This word is not in the dictionary!");
                                return;
                            }
                            
                            if (instance.hasGuessed(word)) {
                                error("You have already guessed this word!");
                                return;
                            }
                            
                            if (instance.hardMode) {
                                final WordleInstance.HardModeResponse hardModeResponse = instance.canGuessInHardMode(word);
                                
                                if (!hardModeResponse.isOk()) {
                                    error(hardModeResponse.whyNotOk());
                                    return;
                                }
                            }
                            
                            instance.guess(word);
                            Message.sound(player, SoundEffect.SUCCESS);
                            
                            // Returns sync
                            runSync(WordleGUI.this::openInventory);
                        }
                        
                        private void error(String message) {
                            Message.error(player, message);
                            Message.sound(player, SoundEffect.FAILURE);
                            
                            runSync(WordleGUI.this::openInventory);
                        }
                    };
                }
        );
        
        // Set hard-mode button
        setItem(
                44, makeHardModeItem(), player -> {
                    if (!instance.guesses().isEmpty()) {
                        Message.error(player, "Hard mode can only be toggled at the start of a round!");
                        Message.sound(player, SoundEffect.FAILURE);
                        return;
                    }
                    
                    instance.hardMode = !instance.hardMode;
                    
                    Message.success(player, "Hard mode is now {%s}!".formatted(instance.hardMode ? "enabled" : "disabled"));
                    Message.sound(player, Sound.ENTITY_SKELETON_HURT, 0.75f);
                    
                    openInventory();
                }
        );
        
        // Check for win/lose after all items are set to you can see the win/lose screen
        if (instance.hasWon()) {
            instance.onWin();
            state = WorldeState.GAME_OVER;
            columns(columnItemWin);
            
            // Update sign
            setItem(
                    singSlot, makeWinItem(), player -> {
                        instance.flex();
                        player.closeInventory();
                    }
            );
            
            // Fx
            Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
            Message.sound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1.25f);
        }
        else if (instance.hasLost()) {
            instance.onLose();
            state = WorldeState.GAME_OVER;
            columns(columnItemLose);
            
            // Update sign
            setItem(singSlot, makeLoseItem(), HumanEntity::closeInventory);
        }
    }
    
    private ItemStack makeHardModeItem() {
        final ItemBuilder builder = ItemBuilder.playerHeadUrl(instance.hardMode ? textureHardModeEnabled : textureHardModeDisabled)
                                               .setName("Hard Mode " + BukkitUtils.checkmark(instance.hardMode))
                                               .addTextBlockLore("""
                                                                 In Hard Mode, any revealed hints must be used in &nsubsequent&7 guesses.
                                                                 
                                                                 All &acorrect&7 letters must remain in their exact positions, and all &epreviously found&7 letters must be reused somewhere in the guess.
                                                                 
                                                                 &8&o;;Hard Mode is an optional challenge and does not provide any additional rewards.
                                                                 """)
                                               .addLore();
        
        // Dynamic button text
        if (!instance.guesses().isEmpty()) {
            builder.addTextBlockLore("""
                                     %s;;Hard mode can only be toggled at the start of a round!
                                     """.formatted(Color.ERROR));
        }
        else {
            builder.addLore(Color.BUTTON + (instance.hardMode ? "Click to disable" : "Click to enable"));
        }
        
        return builder.asIcon();
    }
    
    private ItemStack makeLoseItem() {
        final ItemBuilder builder = new ItemBuilder(Material.REDSTONE)
                .setName("&cYou Lose :(")
                .addLore();
        
        return appendWordDefinition(builder)
                .addLore()
                .addLore(Color.BUTTON + "Click to cry")
                .asIcon();
    }
    
    private ItemStack makeWinItem() {
        final ItemBuilder builder = new ItemBuilder(Material.EMERALD)
                .setName("You Won!")
                .addLore("&8" + instance.winStatement())
                .addLore();
        
        // Add stats and definition
        builder.addLore("You guessed in &b%s&7 tries!".formatted(instance.guesses().size()));
        builder.addLore();
        
        // Add definition
        return appendWordDefinition(builder)
                .addLore()
                .addLore(Color.BUTTON + "Click to flex!")
                .asIcon();
    }
    
    private ItemBuilder appendWordDefinition(ItemBuilder builder) {
        final WordleWord hiddenWord = instance.hiddenWord();
        builder.addTextBlockLore("""
                                 &f&l%s
                                 &7&o;;%s
                                 """.formatted(hiddenWord.word().toUpperCase(), hiddenWord.definition()));
        
        return builder;
    }
    
    private void columns(ItemStack stack) {
        fillColumn(0, stack);
        fillColumn(1, stack);
        
        fillColumn(7, stack);
        fillColumn(8, stack);
    }
    
    private ItemStack createSignItem() {
        final ItemBuilder sign = new ItemBuilder(Material.OAK_SIGN)
                .setName(Color.SUCCESS + "Click to Guess!")
                .addTextBlockLore("""
                                  Enter a five letter word to guess.
                                  """)
                .addLore();
        
        StringBuilder builder = new StringBuilder();
        
        for (char c : qwerty) {
            builder.append(instance.charValue(c).color(c)).append(" ");
            
            if (c == 'P' || c == 'L' || c == 'M') {
                sign.addLore((c == 'P' ? "      " : c == 'L' ? "       " : "          ") + builder.toString().trim());
                builder = new StringBuilder();
            }
        }
        
        return sign.addLore().addLore(Color.BUTTON + "Click to guess!").build();
    }
    
}
