package me.hapyl.fight.game.cosmetic.gadget.wordle;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.StaticReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WordleInstance {
    
    private final Player player;
    private final WordleWord hiddenWord;
    private final List<WordleGuess> guesses;
    
    protected boolean hardMode;
    
    public WordleInstance(@Nonnull Player player, @Nonnull WordleWord hiddenWord) {
        this.player = player;
        this.hiddenWord = hiddenWord;
        this.guesses = Lists.newArrayList();
    }
    
    @Nonnull
    public String type() {
        return "Daily";
    }
    
    @EventLike
    public void onWin() {
        saveInstance();
        
        // Give reward
        final Reward reward = reward();
        
        if (reward != null) {
            reward.grant(player, true);
        }
    }
    
    @EventLike
    public void onLose() {
        saveInstance();
        
        // Display the correct word
        Chat.sendMessage(player, "&6&lᴡᴏʀᴅʟᴇ!&a Better luck next time!");
        
        Chat.sendHoverableMessage(
                player,
                "&f&l%s\n&7&o%s".formatted(hiddenWord.word().toUpperCase(), hiddenWord.definition()),
                "&6&lᴡᴏʀᴅʟᴇ!&e &6&l&nHOVER&7 to reveal the word."
        );
        
        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_WOLF_WHINE, 0.0f);
    }
    
    @Nonnull
    public Player player() {
        return player;
    }
    
    @Nonnull
    public WordleWord hiddenWord() {
        return hiddenWord;
    }
    
    @Nonnull
    public List<WordleGuess> guesses() {
        return List.copyOf(guesses);
    }
    
    @Nonnull
    public CharacterValue charValue(char c) {
        return guesses.stream()
                      .map(guess -> guess.charValue(Character.toLowerCase(c)))
                      .filter(Objects::nonNull)
                      .min(Comparator.comparingInt(Enum::ordinal))
                      .orElse(CharacterValue.HAS_NOT_GUESSED);
        
    }
    
    public boolean hasGuessed(@Nonnull String string) {
        return guesses.stream().anyMatch(word -> word.guess().equalsIgnoreCase(string));
    }
    
    public void guess(@Nonnull String string) {
        guesses.add(new WordleGuess(string, hiddenWord));
    }
    
    public boolean hasWon() {
        return guesses.stream().anyMatch(guess -> guess.guess().equalsIgnoreCase(hiddenWord.word()));
    }
    
    public boolean hasLost() {
        return guesses.size() >= Wordle.MAX_GUESSSES;
    }
    
    public void flex() {
        final PlayerProfile profile = CF.getProfile(player);
        
        final TextComponent.Builder builder
                = Component.text()
                           .append(Component.text("ᴡᴏʀᴅʟᴇ!", Color.GOLD, TextDecoration.BOLD))
                           .append(Component.text(" ").append(profile.display().toComponent()))
                           .append(Component.text(" guessed wordle", Color.GRAY))
                           .append(Component.text(" #%s".formatted(hiddenWord.index() + 1), Color.AQUA))
                           .append(typeAsComponent())
                           .append(Component.text(" (%s/%s)".formatted(guesses.size(), Wordle.MAX_GUESSSES), Color.DARK_GRAY))
                           .append(Component.text(" HOVER", Color.YELLOW, TextDecoration.BOLD))
                           .hoverEvent(HoverEvent.showText(makeCubes()));
        
        
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(builder);
        });
        
    }
    
    @Nonnull
    public String winStatement() {
        return switch (guesses.size()) {
            case 1 -> "Genius";
            case 2 -> "Magnificent";
            case 3 -> "Impressive";
            case 4 -> "Splendid";
            case 5 -> "Great";
            case 6 -> "Phew";
            default -> "didn't win";
        };
    }
    
    @Nonnull
    public WordleResult result() {
        return new WordleResult(this);
    }
    
    @Nonnull
    public HardModeResponse canGuessInHardMode(@Nonnull String word) {
        Validate.isTrue(word.length() == Wordle.WORD_LENGTH, "There must be exactly %s letters, not %s!".formatted(Wordle.WORD_LENGTH, word.length()));
        
        word = word.toLowerCase(); // Force lower case
        
        // First pass - check for exact position
        for (WordleGuess guess : guesses) {
            for (int i = 0; i < guess.charValue.length; i++) {
                final WordleGuess.Entry entry = guess.charValue[i];
                final char ch = word.charAt(i);
                
                if (entry.value() == CharacterValue.CORRECT && ch != entry.c()) {
                    return HardModeResponse.notOk("%s letter must be '%s'!".formatted(Chat.stNdTh(i + 1), Character.toUpperCase(entry.c())));
                }
            }
        }
        
        // Second pass - check for presence
        for (WordleGuess guess : guesses) {
            for (int i = 0; i < guess.charValue.length; i++) {
                final WordleGuess.Entry entry = guess.charValue[i];
                
                if (entry.value() == CharacterValue.WRONG_POSITION && !word.contains(Character.toString(entry.c()))) {
                    return HardModeResponse.notOk("Must contain letter '%s'!".formatted(Character.toUpperCase(entry.c())));
                }
            }
        }
        
        // Otherwise ok
        return HardModeResponse.ok();
    }
    
    @EventLike // Called whenever player closes the GUI
    public void onQuit() {
    }
    
    @Nullable
    protected Reward reward() {
        return StaticReward.DAILY_WORDLE;
    }
    
    protected void saveInstance() {
        CF.getDatabase(player).wordleEntry.result(this);
    }
    
    private Component typeAsComponent() {
        final TextComponent.Builder builder = Component.text()
                                                       .append(Component.text(" (", Color.GRAY))
                                                       .append(Component.text(type(), Color.GRAY));
        
        if (hardMode) {
            builder.append(Component.text(",", Color.GRAY), Component.text(" ☠", Color.BLOOD));
        }
        
        return builder.append(Component.text(")", Color.GRAY)).build();
    }
    
    private Component makeCubes() {
        final TextComponent.Builder builder = Component.text();
        final Component[] lines = guesses.stream()
                                         .map(guess -> {
                                             final TextComponent.Builder line = Component.text();
                                             
                                             for (WordleGuess.Entry entry : guess.charValue) {
                                                 line.append(Component.text("⬛", entry.value().color()));
                                             }
                                             
                                             return line.asComponent();
                                         })
                                         .toArray(Component[]::new);
        
        for (int i = 0; i < lines.length; i++) {
            if (i != 0) {
                builder.appendNewline();
            }
            
            builder.append(lines[i]);
        }
        
        return builder.build();
    }
    
    public interface HardModeResponse {
        
        boolean isOk();
        
        @Nonnull
        default String whyNotOk() {
            return "";
        }
        
        @Nonnull
        static HardModeResponse ok() {
            return () -> true;
        }
        
        @Nonnull
        static HardModeResponse notOk(@Nonnull String whyNotOk) {
            return new HardModeResponse() {
                @Override
                public boolean isOk() {
                    return false;
                }
                
                @Nonnull
                @Override
                public String whyNotOk() {
                    return whyNotOk;
                }
            };
        }
        
    }
    
}
