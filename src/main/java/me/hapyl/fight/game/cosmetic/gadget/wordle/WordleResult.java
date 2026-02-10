package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.fight.database.serialize.MongoSerializable;
import me.hapyl.fight.database.serialize.MongoSerializableConstructor;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.List;

public class WordleResult implements MongoSerializable, Comparable<WordleResult> {
    
    private int index;
    private String hiddenWord;
    private List<String> guesses;
    private boolean hardMode;
    private long timeStamp;
    private boolean isWin;
    
    public WordleResult(@Nonnull WordleInstance instance) {
        this.index = instance.hiddenWord().index();
        this.hiddenWord = instance.hiddenWord().word();
        this.guesses = instance.guesses().stream().map(WordleGuess::guess).toList();
        this.hardMode = instance.hardMode;
        this.timeStamp = System.currentTimeMillis();
        this.isWin = instance.hasWon();
    }
    
    @MongoSerializableConstructor
    private WordleResult() {
    }
    
    public int index() {
        return index;
    }
    
    @Nonnull
    public String hiddenWord() {
        return hiddenWord;
    }
    
    @Nonnull
    public List<String> guesses() {
        return guesses;
    }
    
    public boolean hardMode() {
        return hardMode;
    }
    
    public long timeStamp() {
        return timeStamp;
    }
    
    public boolean isWin() {
        return isWin;
    }
    
    @Nonnull
    @Override
    public Document serialize() {
        return new Document()
                .append("index", this.index)
                .append("hidden_word", this.hiddenWord)
                .append("guesses", this.guesses)
                .append("hard_mode", this.hardMode)
                .append("time_stamp", this.timeStamp)
                .append("is_win", this.isWin);
    }
    
    @Override
    public void deserialize(@Nonnull Document document) {
        this.index = document.getInteger("index");
        this.hiddenWord = document.getString("hidden_word");
        this.guesses = document.getList("guesses", String.class);
        this.hardMode = document.getBoolean("hard_mode");
        this.timeStamp = document.getLong("time_stamp");
        this.isWin = document.getBoolean("is_win");
    }
    
    @Override
    public int compareTo(@Nonnull WordleResult that) {
        return Integer.compare(this.index, that.index);
    }
    
    @Nonnull
    @Override
    public String toString() {
        return "WordleResult{" +
                "index=" + index +
                ", hiddenWord='" + hiddenWord + '\'' +
                ", guesses=" + guesses +
                ", hardMode=" + hardMode +
                ", timeStamp=" + timeStamp +
                ", isWin=" + isWin +
                '}';
    }
}
