package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.util.Labelled;

import javax.annotation.Nonnull;

public class GuessWhoEntry extends PlayerDatabaseEntry {
    public GuessWhoEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "guess_who");
    }
    
    public long getStat(@Nonnull StatType type) {
        return getValue(type.getKeyAsString(), 0L);
    }
    
    public void incrementStat(@Nonnull StatType type) {
        setValue(type.getKeyAsString(), getStat(type) + 1);
    }
    
    public void resetStat(@Nonnull StatType type) {
        setValue(type.getKeyAsString(), 0L);
    }
    
    public enum StatType implements Labelled, KeyedEnum {
        PLAYED,
        WINS,
        WIN_PERCENT {
            @Nonnull
            @Override
            public String asString(@Nonnull GuessWhoEntry entry) {
                final long played = entry.getStat(PLAYED);
                final long wins = entry.getStat(WINS);
                
                return played == 0 ? "0%" : "%.1f%%".formatted((double) wins / played);
            }
            
            @Override
            public boolean isStorable() {
                return false;
            }
        },
        WIN_STREAK,
        LOSES,
        FORFEITS;
        
        private final String label;
        
        StatType() {
            this.label = Chat.capitalize(this);
        }
        
        @Nonnull
        @Override
        public String label() {
            return label;
        }
        
        public boolean isStorable() {
            return true;
        }
        
        @Nonnull
        @Override
        public String getKeyAsString() {
            if (!isStorable()) {
                throw new IllegalStateException("Illegal to store %s in the database!".formatted(this));
            }
            
            return KeyedEnum.super.getKeyAsString();
        }
        
        @Nonnull
        public String asString(@Nonnull GuessWhoEntry entry) {
            return String.valueOf(entry.getStat(this));
        }
        
    }
    
}
