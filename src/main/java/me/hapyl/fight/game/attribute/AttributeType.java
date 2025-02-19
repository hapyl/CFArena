package me.hapyl.fight.game.attribute;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiFunction;

public enum AttributeType implements Described {

    MAX_HEALTH(
            new Attribute("Max Health", "Maximum health of an entity.") {
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
        public double minValue() {
            return 0.5d;
        }

        @Override
        public double getScale() {
            return 1;
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
            new Attribute("Speed", "Movement speed of an entity.") {

                @Override
                public void update(LivingGameEntity entity, double value) {
                    if (entity.getWalkSpeed() == value) {
                        return;
                    }

                    entity.setWalkSpeed(Numbers.clamp1neg1((float) value));
                }
            }.setChar("🌊")
             .setColor(ChatColor.AQUA)
             .setToString((type, value) -> "%.0f%%".formatted(type.scaleUp(value))),
            0.2d
    ) {
        @Override
        public double maxValue() {
            return 1.0d;
        }

        @Override
        public double getScale() {
            return 500;
        }
    },
    CRIT_CHANCE(
            new Attribute("Crit Chance", "Chance for attack to deal critical hit.")
                    .setChar("☣")
                    .setColor(ChatColor.BLUE)
                    .setToString(AttributeType::doubleFormatPercent),
            0.1d
    ) {
        @Override
        public double minValue() {
            return -1;
        }
    },
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
            new Attribute("Mending", "Outgoing healing multiplier.")
                    .setChar("🌿")
                    .setColor(ChatColor.GREEN)
                    .setToString(AttributeType::doubleFormatPercent),
            1.0d
    ),
    VITALITY(
            new Attribute("Vitality", "Incoming healing multiplier.")
                    .setChar(ChatColor.BOLD + "\uD83E\uDE78" + ChatColor.DARK_RED)
                    .setColor(ChatColor.DARK_RED)
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
            new Attribute("Cooldown Modifier", "The modifier of your cooldown talents.")
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

    ATTACK_SPEED(
            new Attribute("Attack Speed", "Decreases the cooldown between your attacks.") {

                private static final NavigableMap<Double, PotionEffect> effectMap;

                static {
                    effectMap = new TreeMap<>();

                    effectMap.put(1.25d, makeEffect(PotionEffectType.HASTE, 0));
                    effectMap.put(1.75d, makeEffect(PotionEffectType.HASTE, 1));
                    effectMap.put(2.0d, makeEffect(PotionEffectType.HASTE, 2));
                    effectMap.put(0.0d, makeEffect(PotionEffectType.MINING_FATIGUE, 8));
                    effectMap.put(0.125d, makeEffect(PotionEffectType.MINING_FATIGUE, 7));
                    effectMap.put(0.25d, makeEffect(PotionEffectType.MINING_FATIGUE, 6));
                    effectMap.put(0.375d, makeEffect(PotionEffectType.MINING_FATIGUE, 5));
                    effectMap.put(0.5d, makeEffect(PotionEffectType.MINING_FATIGUE, 4));
                    effectMap.put(0.625d, makeEffect(PotionEffectType.MINING_FATIGUE, 3));
                    effectMap.put(0.75d, makeEffect(PotionEffectType.MINING_FATIGUE, 2));
                    effectMap.put(0.875d, makeEffect(PotionEffectType.MINING_FATIGUE, 1));
                }

                @Override
                @SuppressWarnings("deprecation")
                public void update(LivingGameEntity entity, double value) {
                    entity.removePotionEffect(PotionEffectType.HASTE);
                    entity.removePotionEffect(PotionEffectType.MINING_FATIGUE);

                    // Apply either haste or mining fatigue for effect only
                    final Map.Entry<Double, PotionEffect> entry = effectMap.floorEntry(value);

                    if (entry != null) {
                        entity.addPotionEffect(entry.getValue());
                    }
                }

                private static PotionEffect makeEffect(PotionEffectType type, int amplifier) {
                    return new PotionEffect(type, Constants.INFINITE_DURATION, amplifier, false, false, false);
                }
            }
                    .setChar("⚔")
                    .setColor(ChatColor.YELLOW)
                    .setToString(AttributeType::doubleFormatPercent),
            1.0d
    ) {
        @Override
        public double minValue() {
            return 0.1d; // 10%
        }

        @Override
        public double maxValue() {
            return 2d; // 200%
        }
    },

    KNOCKBACK_RESISTANCE(
            new Attribute("Knockback Resistance", "Multiplier on how much knockback resistance you have.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE, value);
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

    EFFECT_RESISTANCE(
            new Attribute("Effect Resistance", "The chance to resist negative effects.")
                    .setChar("&5&l\uD83D\uDC1A&5")
                    .setColor(ChatColor.DARK_PURPLE),
            0.0
    ) {
        @Override
        public double maxValue() {
            return 1.0d;
        }
    },

    HEIGHT(
            new Attribute("Height", "Height doesn't matter. Or does it?") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.SCALE, value);
                }
            }
                    .setChar("\uD83D\uDCCF")
                    .setColor(ChatColor.GREEN)
                    .setToString((t, d) -> {
                        return "%.0f cb".formatted(t.scaleUp(d));
                    }),
            1.0d
    ) {
        @Override
        public double getScale() {
            return 180; // Steve is 1.8 blocks tall so that's scale duh
        }
    },

    ENERGY_RECHARGE(
            new Attribute("Energy Recharge", "Multiplier on how fast you generate energy.")
                    .setChar("♻")
                    .setColor(ChatColor.DARK_AQUA),
            1.0d
    ),

    JUMP_STRENGTH(
            new Attribute("Jump Strength", "How high you jump.") {
                @Override
                public void update(LivingGameEntity entity, double value) {
                    entity.setAttributeValue(org.bukkit.attribute.Attribute.JUMP_STRENGTH, value);
                }
            }
                    .setChar("🐇")
                    .setColor(ChatColor.AQUA)
                    .setToString(AttributeType::doubleFormatScaled),
            0.45
    ) {
        @Override
        public double maxValue() {
            return 6.5d;
        }

        @Override
        public double getScale() {
            return 222.22222222222223d;
        }
    },

    DEFENSE_IGNORE(
            new Attribute("Defense Ignore", "The percentage of victim's defense ignored when dealing damage.")
                    .setChar("∅")
                    .setColor(ChatColor.GOLD)
                    .setToString(AttributeType::doubleFormatPercent),
            0.0d
    ),

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

    public final boolean isMandatory() {
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
     * <br>
     * If <code>true</code>, relativity means `The higher, the better`.
     * <br>
     * If <code>false</code>, relativity means 'The lower, the better'.
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

    public double getDefaultValueScaled() {
        return scaleUp(defaultValue);
    }

    /**
     * Gets the current value of this attribute from the given {@link BaseAttributes}.
     *
     * @param attributes - Attributes.
     * @return the current value of thia attribute from the given attributes.
     */
    public double get(@Nonnull BaseAttributes attributes) {
        return attributes.get(this);
    }

    @Override
    public String toString() {
        final ChatColor color = attribute.getColor();

        return color + attribute.getCharacter() + " " + color + getName() + "&7";
    }

    @Nonnull
    public ChatColor getColor() {
        return attribute.getColor();
    }

    @Nonnull
    public String getFormatted(@Nonnull BaseAttributes attributes) {
        return doFormat(attributes, AttributeType::toString);
    }

    @Nonnull
    public String getFormattedScaled(@Nonnull BaseAttributes attributes) {
        return doFormat(attributes, (t, v) -> t.toString(t.scaleUp(v)));
    }

    public boolean isBuff(double newValue, double oldValue) {
        return relativity() ? newValue > oldValue : newValue < oldValue;
    }

    /**
     * Scales the value up, meaning multiplying the value by <code>scale</code>.
     *
     * @param value - Value to scale.
     * @return scaled value.
     * @see #getScale()
     */
    public double scaleUp(double value) {
        return value * getScale();
    }

    /**
     * Scales the value down, meaning dividing the value by <code>scale</code>.
     *
     * @param value - Value to scale.
     * @return scaled value.
     * @see #getScale()
     */
    public double scaleDown(double value) {
        return value / getScale();
    }

    /**
     * Gets the scale of this attribute.
     * <p>
     * Since most attribute values are normalized, scaling up or down is
     * required to display or setting the value in more human way.
     * <p>
     * Examples:
     * <pre>
     *     // More clear that the crit is 10%
     *     setCritChance(10);
     *
     *     // Less clear that the crit is 10%, moreover, some attributes scale differently, like speed.
     *     setCritChance(0.1);
     *
     * </pre>
     *
     * <h2>The speed is the best example:</h2>
     * <pre>
     *     // Very clear that the speed is 25%
     *     setSpeed(25);
     *
     *     // Not very clear it's 25%.
     *     setSpeed(0.05d);
     * </pre>
     *
     * @return the scale of this attribute.
     */
    public double getScale() {
        return 100;
    }

    @Nonnull
    public String toString(double value) {
        return attribute.toString(this, value);
    }

    @Nonnull
    public String getCharacter() {
        return attribute.getCharacter();
    }

    private String doFormat(BaseAttributes attributes, BiFunction<AttributeType, Double, String> fn) {
        final double value = get(attributes);

        return "%s%s %s".formatted(attribute.getColor(), attribute.getCharacter(), fn.apply(this, value));
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
    public static String doubleFormat(@Nonnull AttributeType type, double value) {
        return "%.0f".formatted(value);
    }

    @Nonnull
    public static String doubleFormatScaled(@Nonnull AttributeType type, double value) {
        return doubleFormat(type, type.scaleUp(value));
    }

    @Nonnull
    public static String doubleFormatPercent(@Nonnull AttributeType type, double value) {
        return doubleFormatScaled(type, value) + "%";
    }

}

