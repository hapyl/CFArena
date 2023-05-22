package me.hapyl.fight.game.attribute;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum AttributeType {

    HEALTH(
            new Attribute("Health", "Maximum health hero has.")
                    .setChar("❤")
                    .setColor(ChatColor.RED)
                    .setToString(String::valueOf),
            100.0d
    ),
    ATTACK(
            new Attribute("Attack", "The more attack you have, the more damage you will deal.")
                    .setChar("🗡")
                    .setColor(ChatColor.DARK_RED),
            1.0d
    ),
    DEFENSE(
            new Attribute("Defense", "The more defense you have, the less damage you will take.")
                    .setChar("🛡")
                    .setColor(ChatColor.DARK_GREEN),
            1.0d
    ),
    SPEED(
            new Attribute("Speed", "Movement speed of the hero.")
                    .setChar("🌊")
                    .setColor(ChatColor.AQUA)
                    .setToString(d -> "+" + (80 + d * 100)),
            0.2d
    ),
    CRIT_CHANCE(
            new Attribute("Critical Chance", "Chance for attack to deal critical hit.")
                    .setChar("☣")
                    .setColor(ChatColor.BLUE)
                    .setToString(d -> (d * 100.0d) + "%"),
            0.1d
    ),
    CRIT_DAMAGE(
            new Attribute("Critical Damage", "The damage increase modifier for critical hit.")
                    .setChar("☠")
                    .setColor(ChatColor.BLUE)
                    .setToString(d -> (d * 100.0d) + "%"),
            0.5d
    );

    public final Attribute attribute;
    private final double defaultValue;

    AttributeType(Attribute attribute, double defaultValue) {
        this.attribute = attribute;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return attribute.getName();
    }

    public String getDescription() {
        return attribute.getDescription();
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double get(Attributes attributes) {
        return attributes.get(this);
    }

    @Nonnull
    public String getFormatted(Attributes attributes) {
        final double value = get(attributes);

        return "%s%s %s".formatted(attribute.getColor(), attribute.getCharacter(), attribute.toString(value));
    }
}

