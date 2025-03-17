package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class Attribute implements Described {

    private final String name;
    private final String description;

    private String character;
    private ChatColor color;

    private BiFunction<AttributeType, Double, String> toString;

    Attribute(String name, String description) {
        this.name = name;
        this.description = description;
        this.character = "?";
        this.color = ChatColor.GREEN;
        this.toString = AttributeType::doubleFormatPercent;
    }

    @Nonnull
    public BiFunction<AttributeType, Double, String> getToString() {
        return toString;
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
    public ChatColor getColor() {
        return color;
    }

    public Attribute setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    public Attribute setToString(@Nonnull BiFunction<AttributeType, Double, String> toString) {
        this.toString = toString;
        return this;
    }

    public Attribute setChar(String character) {
        this.character = character;
        return this;
    }

    /**
     * Called every time an attribute updates.
     *
     * @param entity - Player.
     * @param value  - The new value.
     */
    public void update(@Nonnull LivingGameEntity entity, double value) {
    }

    @Nonnull
    public String toString(@Nonnull AttributeType type, double value) {
        return toString.apply(type, value);
    }
}
