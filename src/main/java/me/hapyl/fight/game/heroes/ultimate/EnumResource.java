package me.hapyl.fight.game.heroes.ultimate;

import me.hapyl.fight.game.NamedColoredPrefixed;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.color.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EnumResource implements NamedColoredPrefixed, Resource {
    
    ENERGY("※", "Energy", Color.AQUA) {
        @Override
        public double passive() {
            return 1.0;
        }
        
        @Override
        public double playerElimination() {
            return 2.0;
        }
        
        @Nonnull
        @Override
        public AttributeType effectiveAttribute() {
            return AttributeType.ENERGY_RECHARGE;
        }
    },
    
    SURGE("&l\uD83D\uDCA0", "Surge", Color.DEEP_SKY) {
        @Override
        public double passive() {
            return 0.0;
        }
        
        @Override
        public double playerElimination() {
            return 0.0;
        }
        
        @Nullable
        @Override
        public AttributeType effectiveAttribute() {
            return null;
        }
    },
    
    ;
    
    private final String prefix;
    private final String name;
    private final Color color;
    
    EnumResource(String character, String name, Color color) {
        this.prefix = character;
        this.name = name;
        this.color = color;
    }
    
    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }
    
    @Nonnull
    @Override
    public String toString() {
        return toString0();
    }
}
