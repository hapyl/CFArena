package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public abstract class Attribute implements IAttribute, Described {
    
    private final String name;
    private final String description;
    
    @Nonnull private String character;
    @Nonnull private Color color;
    @Nonnull private AttributeFormat format;
    
    Attribute(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        this.character = "💀";
        this.color = Color.DEFAULT;
        this.format = AttributeFormat.FLAT;
    }
    
    @Override
    public double minValue() {
        return 0;
    }
    
    @Override
    public double maxValue() {
        return 1_000;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
    
    @Nonnull
    public String getCharacter() {
        return character;
    }
    
    @Nonnull
    @Override
    public Color getColor() {
        return color;
    }
    
    public void update(@Nonnull LivingGameEntity entity, double value) {
    }
    
    @Nonnull
    @Override
    public String toString() {
        return "%1$s%2$s %1$s%3$s&7".formatted(color, character, getName());
    }
    
    @Nonnull
    @Override
    public String toString(double value) {
        return format.format(value);
    }
    
    @SelfReturn
    protected Attribute color(@Nonnull Color color) {
        this.color = color;
        return this;
    }
    
    @SelfReturn
    protected Attribute character(@Nonnull String character) {
        this.character = character;
        return this;
    }
    
    @SelfReturn
    public Attribute format(@Nonnull AttributeFormat format) {
        this.format = format;
        return this;
    }
}
