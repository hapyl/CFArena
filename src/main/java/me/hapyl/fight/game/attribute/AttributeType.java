package me.hapyl.fight.game.attribute;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.List;

public enum AttributeType { // implements Placeholder2

    MAX_HEALTH(
            new Attribute("Health", "Maximum health hero has.")
                    .setChar("❤")
                    .setColor(ChatColor.RED)
                    .setToString(String::valueOf),
            100.0d
    ) {
        @Override
        public double maxValue() {
            return 1_000_000_000;
        }
    },
    ATTACK(
            new Attribute("Attack", "The more attack you have, the more damage you deal.")
                    .setChar("🗡")
                    .setColor(ChatColor.DARK_RED),
            1.0d
    ),
    DEFENSE(
            new Attribute("Defense", "The more defense you have, the less damage you take.")
                    .setChar("🛡")
                    .setColor(ChatColor.DARK_GREEN),
            1.0d
    ),
    SPEED(
            new Attribute("Speed", "Movement speed of the hero.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    if (entity.getWalkSpeed() == value) {
                        return;
                    }

                    entity.setWalkSpeed(Numbers.clamp1neg1((float) value));
                }
            }.setChar("🌊").setColor(ChatColor.AQUA).setToString(value -> {
                final double proportion = (value - 0.2d) / (1.0d - 0.2d);
                final double increase = (proportion * 400.0d) + 100;

                return "%.2f%%".formatted(increase);
            }),
            0.2d
    ) {
        @Override
        public double maxValue() {
            return 1.0d;
        }
    },
    CRIT_CHANCE(
            new Attribute("CRIT Chance", "Chance for attack to deal critical hit.")
                    .setChar("☣")
                    .setColor(ChatColor.BLUE)
                    .setToString(d -> "%.2f%%".formatted(d * 100.0d)),
            0.1d
    ),
    CRIT_DAMAGE(
            new Attribute("CRIT Damage", "The damage increase modifier for critical hit.")
                    .setChar("☠")
                    .setColor(ChatColor.BLUE)
                    .setToString(d -> "%.2f%%".formatted(d * 100.0d)),
            0.5d
    ),

    FEROCITY(
            new Attribute("Ferocity", "The change to deal an extra strike.")
                    .setChar("\uD83C\uDF00")
                    .setColor(ChatColor.RED)
                    .setToString(d -> "%.2f%%".formatted(d * 100.0d)),
            0
    ) {
        @Override
        public double maxValue() {
            return 5;
        }
    };

    private static final List<String> NAMES;

    static {
        NAMES = Lists.newArrayList();

        for (AttributeType value : values()) {
            NAMES.add(value.name());
        }
    }

    public final Attribute attribute;
    private final double defaultValue;

    AttributeType(Attribute attribute, double defaultValue) {
        this.attribute = attribute;
        this.defaultValue = defaultValue;
    }

    public double minValue() {
        return 0.0d;
    }

    public double maxValue() {
        return 10.0d;
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

    @Override
    public String toString() {
        return attribute.getColor() + attribute.getCharacter() + " " + getName() + "&7";
    }

    @Nonnull
    public String getFormatted(Attributes attributes) {
        final double value = get(attributes);

        return "%s%s %s".formatted(attribute.getColor(), attribute.getCharacter(), attribute.toString(value));
    }

    public static List<String> names() {
        return Lists.newArrayList(NAMES);
    }

}

