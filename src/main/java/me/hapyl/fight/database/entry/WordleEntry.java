package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.database.serialize.MongoSerializable;
import me.hapyl.fight.game.cosmetic.gadget.wordle.*;
import org.bson.Document;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class WordleEntry extends PlayerDatabaseEntry {
    
    @Nullable private WordleInstance instance;
    
    public WordleEntry(@Nonnull PlayerDatabase database) {
        super(database, "wordle");
    }
    
    /// <pre>
    /// wordle:
    /// {
    ///     today:
    ///     {
    ///         day: 0,     // denotes the current day
    ///         guesses: [] // denotes the player's guesses
    ///     },
    ///     history:
    ///     {
    ///         0: {}
    ///     }
    /// }
    /// </pre>
    
    @Nonnull
    public WordleInstance today() {
        final Document document = getDocument();
        final int todayIndex = Wordle.todayIndex();
        final Player player = player().orElseThrow(() -> new IllegalStateException("Unavailable for offline players!"));
        
        // Try to load today's instance
        if (instance == null) {
            final Document today = document.get("today", new Document());
            final int storedIndex = today.get("day", -1);
            
            // If the day is the default value, means never played, create new instance
            if (storedIndex == -1) {
                // This always create a daily word instance, practice and test instances are NOT persistent
                instance = new WordleInstance(player, Wordle.todayWord());
            }
            // Else try parse
            else {
                final WordleWord byIndex = Objects.requireNonNull(Wordle.byIndex(storedIndex), "Illegal index: " + storedIndex);
                final WordleInstance instance = new WordleInstance(player, byIndex);
                
                // Add guesses
                for (String guess : today.getList("guesses", String.class, Lists.newArrayList())) {
                    instance.guess(guess);
                }
                
                // If the day has changed, store the instance in history and return a new instance for this day
                if (storedIndex != todayIndex) {
                    result(instance);
                    
                    this.instance = new WordleInstance(player, Wordle.todayWord());
                }
                else {
                    this.instance = instance;
                }
            }
        }
        
        return instance;
    }
    
    @Override
    public void onSave() {
        // Preserve the current instance
        if (instance == null) {
            return;
        }
        
        getDocument().put(
                "today",
                new Document()
                        .append("day", instance.hiddenWord().index())
                        .append("guesses", instance.guesses().stream().map(WordleGuess::guess).toList())
        );
    }
    
    public void result(@Nonnull WordleInstance instance) {
        final WordleResult result = instance.result();
        
        fetchDocument("history", document -> document.put(stringIndex(result.index()), result.serialize()));
    }
    
    @Nullable
    public WordleResult result(int intIndex) {
        final Document document = getDocument();
        final Document history = document.get("history", new Document());
        final String index = stringIndex(intIndex);
        
        if (!history.containsKey(index)) {
            return null;
        }
        
        return MongoSerializable.deserialize(WordleResult.class, history.get(index, Document.class));
    }
    
    @Nonnull
    public List<WordleResult> results() {
        return getDocument().get("history", new Document())
                            .values()
                            .stream()
                            .filter(obj -> obj instanceof Document)
                            .map(obj -> MongoSerializable.deserialize(WordleResult.class, (Document) obj))
                            .sorted(WordleResult::compareTo)
                            .toList();
    }
    
    private static String stringIndex(int intIndex) {
        return Integer.toString(intIndex);
    }
    
}
