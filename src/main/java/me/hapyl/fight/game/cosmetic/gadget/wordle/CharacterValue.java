package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;

public enum CharacterValue {
    
    CORRECT(Color.GREEN),
    WRONG_POSITION(Color.YELLOW),
    INCORRECT(Color.DARK_GRAY) {
        @Nonnull
        @Override
        public String color(@Nonnull Object obj) {
            return color() + String.valueOf(obj); // Make incorrect not bold
        }
    },
    HAS_NOT_GUESSED(Color.WHITE);
    
    private final Color color;
    
    CharacterValue(@Nonnull Color color) {
        this.color = color;
    }
    
    @Nonnull
    public Color color() {
        return color;
    }
    
    @Nonnull
    public String color(@Nonnull Object obj) {
        return color.bold() + obj;
    }
    
    @Override
    public String toString() {
        return color + "⬛";
    }
}
