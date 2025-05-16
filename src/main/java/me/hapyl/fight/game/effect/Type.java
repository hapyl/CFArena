package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;

public enum Type {
    
    /**
     * Defines a neutral effect.
     */
    NEUTRAL("Neutral", Color.YELLOW),
    
    /**
     * Defines a positive effect.
     */
    POSITIVE("Positive", Color.GREEN),
    
    /**
     * Defines a negative effect.
     */
    NEGATIVE("Negative", Color.RED);
    
    private final String name;
    private final Color color;
    
    Type(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    
    @Nonnull
    public String getName() {
        return name;
    }
    
    @Nonnull
    public Color getColor() {
        return color;
    }
}
