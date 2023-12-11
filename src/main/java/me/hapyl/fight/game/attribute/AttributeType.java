package me.hapyl.fight.game.attribute;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.List;

public enum AttributeType implements Described {

    MAX_HEALTH(
            new Attribute("Health", "Maximum health hero has.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    final double health = entity.getHealth();

                    if (health > value) {
                        entity.setHealth(value);
                    }
                    else {
                        if (!(entity instanceof GamePlayer player)) {
                            return;
                        }

                        player.updateHealth();
                    }
                }
            }
                    .setChar("❤")
                    .setColor(ChatColor.RED)
                    .setToString(AttributeType::doubleFormat),
            100.0d
    ) {
        @Override
        public double maxValue() {
            return 1_000_000_000;
        }

        @Override
        public double scale(double value) {
            return value;
        }
    },
    ATTACK(
            new Attribute("Attack", "The more attack you have, the more damage you deal.")
                    .setChar("🗡")
                    .setColor(ChatColor.DARK_RED)
                    .setToString(AttributeType::doubleFormatScaled),
            1.0d
    ),
    DEFENSE(
            new Attribute("Defense", "The more defense you have, the less damage you take.")
                    .setChar("🛡")
                    .setColor(ChatColor.DARK_GREEN)
                    .setToString(AttributeType::doubleFormatScaled),
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
            }.setChar("🌊")
                    .setColor(ChatColor.AQUA)
                    .setToString(value -> "%.0f%%".formatted(value / 0.002d)),
            0.2d
    ) {
        @Override
        public double maxValue() {
            return 1.0d;
        }

        @Override
        public double scale(double value) {
            return value * 0.002d;
        }
    },
    CRIT_CHANCE(
            new Attribute("Crit Chance", "Chance for attack to deal critical hit.")
                    .setChar("☣")
                    .setColor(ChatColor.BLUE)
                    .setToString(AttributeType::doubleFormatPercent),
            0.1d
    ),
    CRIT_DAMAGE(
            new Attribute("Crit Damage", "The damage increase modifier for critical hit.")
                    .setChar("☠")
                    .setColor(ChatColor.BLUE)
                    .setToString(AttributeType::doubleFormatPercent),
            0.5d
    ),
    FEROCITY(
            new Attribute("Ferocity", "The change to deal an extra strike.")
                    .setChar("\uD83C\uDF00")
                    .setColor(ChatColor.RED)
                    .setToString(AttributeType::doubleFormatPercent),
            0
    ) {
        @Override
        public double maxValue() {
            return 5; // 500% or 5 strikes
        }
    },
    MENDING(
            new Attribute("Mending", "Incoming healing multiplier.")
                    .setChar("🌿")
                    .setColor(ChatColor.GREEN)
                    .setToString(AttributeType::doubleFormatPercent),
            1.0d
    ),
    DODGE(
            new Attribute("Dodge", "Chance to dodge and nullity an attack.")
                    .setChar("\uD83D\uDC65")
                    .setColor(ChatColor.GOLD)
                    .setToString(AttributeType::doubleFormatPercent),
            0.0d
    ) {
        @Override
        public double maxValue() {
            return 0.8d;
        }
    },

    COOLDOWN_MODIFIER(
            new Attribute("Cooldown Modifier", "The modifier of your cooldown abilities.")
                    .setChar("\uD83D\uDD02")
                    .setColor(ChatColor.DARK_GREEN)
                    .setToString(AttributeType::doubleFormatPercent),
            1.0
    ) {
        @Override
        public boolean relativity() {
            return false;
        }
    },

    // TODO -> Maybe implement for bows/range weapons
    ATTACK_SPEED(
            new Attribute("Attack Speed", "How fast you attack.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED, value);
                }
            }
                    .setChar("⚔")
                    .setColor(ChatColor.YELLOW)
                    .setToString((value) -> {
                        return doubleFormatPercent(value - 1);
                    }),
            2.0d
    ) {
        @Override
        public double maxValue() {
            return 10;
        }
    },

    KNOCKBACK_RESISTANCE(
            new Attribute("Knockback Resistance", "Multiplier on how much knockback resistance you have.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE, value);
                }
            }
                    .setChar("🦏")
                    .setColor(ChatColor.DARK_AQUA),
            0.0d
    ) {
        @Override
        public double maxValue() {
            return 1.0d;
        }
    },

    ;

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

    public boolean isMandatory() {
        return switch (this) {
            case MAX_HEALTH, ATTACK, DEFENSE, SPEED, CRIT_CHANCE, CRIT_DAMAGE, ATTACK_SPEED -> true;
            default -> false;
        };
    }

    /**
     * Gets the minimum value that this attribute may be.
     *
     * @return the minimum value.
     */
    public double minValue() {
        return 0.0d;
    }

    /**
     * Gets the maximum value that this attribute may be.
     *
     * @return the maximum value.
     */
    public double maxValue() {
        return 10.0d;
    }

    /**
     * Gets the relativity of this attribute.
     * If true, relativity means `The higher, the better`.
     * If false, relativity means 'The lower, the better'.
     *
     * @return the relativity of this attribute.
     */
    public boolean relativity() {
        return true;
    }

    /**
     * Gets the name of this attribute.
     *
     * @return the name of this attribute.
     */
    @Nonnull
    @Override
    public String getName() {
        return attribute.getName();
    }

    /**
     * Gets the description of this attribute.
     *
     * @return the description of this attribute.
     */
    @Nonnull
    @Override
    public String getDescription() {
        return attribute.getDescription();
    }

    /**
     * Gets the default value of this attribute.
     *
     * @return the default value of this attribute.
     */
    public double getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the current value of this attribute from the given {@link Attributes}.
     *
     * @param attributes - Attributes.
     * @return the current value of thia attribute from the given attributes.
     */
    public double get(@Nonnull Attributes attributes) {
        return attributes.get(this);
    }

    @Override
    public String toString() {
        return attribute.getColor() + attribute.getCharacter() + " " + getName() + "&7";
    }

    @Nonnull
    public String getDecimalFormatted(Attributes attributes) {
        return CFUtils.decimalFormat(get(attributes) * 100.0d);
    }

    @Nonnull
    public String getFormatted(Attributes attributes) {
        final double value = get(attributes);

        return "%s%s %s".formatted(attribute.getColor(), attribute.getCharacter(), attribute.toString(value));
    }

    public boolean getDisplayType(double newValue, double oldValue) {
        return relativity() ? newValue > oldValue : newValue < oldValue;
    }

    public double scale(double value) {
        return value / 100;
    }

    /**
     * Gets a copy of attribute names as a new list.
     *
     * @return a copy of attribute names.
     */
    @Nonnull
    public static List<String> listNames() {
        return Lists.newArrayList(NAMES);
    }

    @Nonnull
    public static String doubleFormat(double d) {
        return "%.0f".formatted(d);
    }

    public static String doubleFormatScaled(double d) {
        return doubleFormat(d * 100);
    }

    @Nonnull
    public static String doubleFormatPercent(double d) {
        return doubleFormatScaled(d) + "%";
    }

}

