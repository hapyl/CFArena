package me.hapyl.fight.game.attribute;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Attribute {

    private final String name;
    private final String description;

    private String character;
    private ChatColor color;

    private Function<Double, String> toString;

    public Attribute(String name, String description) {
        this.name = name;
        this.description = description;
        this.character = "|";
        this.color = ChatColor.GREEN;
        this.toString = d -> "%.2f".formatted(d * 100.0d);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCharacter() {
        return character;
    }

    public ChatColor getColor() {
        return color;
    }

    public Attribute setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    public Attribute setToString(@Nonnull Function<Double, String> toString) {
        this.toString = toString;
        return this;
    }

    public Attribute setChar(String character) {
        this.character = character;
        return this;
    }

    /**
     * Called every time {@link PlayerAttributes} calls {@link PlayerAttributes#get(AttributeType)} method.
     *
     * @param player - Player.
     * @param value  - The actual value.
     */
    public void update(Player player, double value) {
    }

    public String toString(double value) {
        return toString.apply(value);
    }
}
