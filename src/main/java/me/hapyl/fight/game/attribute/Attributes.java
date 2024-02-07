package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.fight.translate.Language;
import me.hapyl.fight.util.WeakCopy;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;

public class Attributes implements WeakCopy {

    public final static double DEFENSE_SCALING = 0.5d;
    public static final double INFINITE_ATTACK_SPEED = 9.99d;

    protected final Map<AttributeType, Double> mapped;
    protected final AttributeRandom random;

    public Attributes(LivingEntity entity) {
        this();
        setHealth(entity.getHealth());
    }

    public Attributes() {
        mapped = Maps.newHashMap();
        random = new AttributeRandom(this);

        // write defaults
        for (AttributeType value : AttributeType.values()) {
            mapped.put(value, value.getDefaultValue());
        }
    }

    /**
     * Calculates the outgoing healing with the formula:
     * <pre>
     *     healing * mending
     * </pre>
     *
     * @param healing - Healing.
     * @return the calculated outgoing healing
     */
    public final double calculateOutgoingHealing(double healing) {
        return healing * get(AttributeType.MENDING);
    }

    /**
     * Calculates the incoming healing with the formula:
     * <pre>
     *     healing * vitality
     * </pre>
     *
     * @param healing - Healing.
     * @return the calculated incoming healing.
     */
    public final double calculateIncomingHealing(double healing) {
        return healing * get(AttributeType.VITALITY);
    }

    /**
     * Calculates the incoming damage with the formula:
     * <pre>
     *     damage / (defense * {@link #DEFENSE_SCALING} + (1 - ({@link #DEFENSE_SCALING}))
     * </pre>
     *
     * @param damage - Damage.
     * @return the calculated incoming damage.
     */
    public final double calculateIncomingDamage(double damage) {
        final double defense = get(AttributeType.DEFENSE);

        return calculateDefense(damage, defense);
    }

    /**
     * Calculates the incoming damage with the formula:
     * <pre>
     *     damage / (defense * {@link #DEFENSE_SCALING} + (1 - ({@link #DEFENSE_SCALING}))
     * </pre>
     *
     * @param damage - Damage.
     * @return the calculated incoming damage.
     */
    public final double calculateDefense(double damage, double defense) {
        return damage / (defense * DEFENSE_SCALING + (1 - DEFENSE_SCALING));
    }

    /**
     * Calculates the outgoing damage with the formula:
     * <pre>
     *     damage * attack
     * </pre>
     *
     * @param damage - Damage.
     * @return the calculated outgoing damage.
     */
    public final double calculateOutgoingDamage(double damage) {
        return damage * AttributeType.ATTACK.get(this);
    }

    /**
     * Calculates the range attack speed with the formula:
     * <pre>
     *     scale = sin(PI / (attackSpeed + 1))
     *     scale = atkSpd < 1 ? PI - (PI / 10) - scale : scale
     *
     *     max(0, speed * scale)
     * </pre>
     */
    public final int calculateRangeAttackSpeed(int speed) {
        final double attackSpeed = get(AttributeType.ATTACK_SPEED);

        double scale = Math.sin(Math.PI / (attackSpeed + 1));
        scale = attackSpeed < 1 ? (Math.PI - Math.PI / 10 - scale) : scale;

        return Math.max(0, (int) (speed * scale));
    }

    public final boolean calculateDodge() {
        return random.checkBound(AttributeType.DODGE);
    }

    public final boolean calculateCrowdControlResistance() {
        return random.checkBound(AttributeType.EFFECT_RESISTANCE);
    }

    public final boolean isCritical() {
        return random.checkBound(AttributeType.CRIT_CHANCE);
    }

    public final double scaleCritical(double damage, boolean isCritical) {
        return isCritical ? damage + (damage * AttributeType.CRIT_DAMAGE.get(this)) : damage;
    }

    /**
     * Gets the {@link AttributeType#MAX_HEALTH} value for this attribute.
     *
     * @return the max health.
     */
    public double getHealth() {
        return get(AttributeType.MAX_HEALTH);
    }

    /**
     * Sets the {@link AttributeType#MAX_HEALTH} value for this attribute.
     *
     * @param value - New value.
     */
    public void setHealth(double value) {
        setValueScaled(AttributeType.MAX_HEALTH, value);
    }

    /**
     * Sets the {@link AttributeType#ATTACK} value for this attribute.
     *
     * @param value - New value.
     */
    public void setAttack(double value) {
        setValueScaled(AttributeType.ATTACK, value);
    }

    /**
     * Sets the {@link AttributeType#DEFENSE} value for this attribute.
     *
     * @param value - New value.
     */
    public void setDefense(double value) {
        setValueScaled(AttributeType.DEFENSE, value);
    }

    /**
     * Sets the {@link AttributeType#SPEED} value for this attribute.
     *
     * @param value - New value.
     */
    public void setSpeed(double value) {
        setValueScaled(AttributeType.SPEED, value);
    }

    /**
     * Sets the {@link AttributeType#CRIT_CHANCE} value for this attribute.
     *
     * @param value - New value.
     */
    public void setCritChance(double value) {
        setValueScaled(AttributeType.CRIT_CHANCE, value);
    }

    /**
     * Sets the {@link AttributeType#CRIT_DAMAGE} value for this attribute.
     *
     * @param value - New value.
     */
    public void setCritDamage(double value) {
        setValueScaled(AttributeType.CRIT_DAMAGE, value);
    }

    /**
     * Sets the {@link AttributeType#FEROCITY} value for this attribute.
     *
     * @param value - New value.
     */
    public void setFerocity(double value) {
        setValueScaled(AttributeType.FEROCITY, value);
    }

    /**
     * Sets the {@link AttributeType#VITALITY} value for this attribute.
     *
     * @param value - New value.
     */
    public void setMending(double value) {
        setValueScaled(AttributeType.MENDING, value);
    }

    public void setVitality(double value) {
        setValueScaled(AttributeType.VITALITY, value);
    }

    /**
     * Sets the {@link AttributeType#DODGE} value for this attribute.
     *
     * @param value - New value.
     */
    public void setDodge(double value) {
        setValueScaled(AttributeType.DODGE, value);
    }

    /**
     * Sets the {@link AttributeType#COOLDOWN_MODIFIER} value for this attribute.
     *
     * @param value - New value.
     */
    public void setCooldownModifier(double value) {
        setValueScaled(AttributeType.COOLDOWN_MODIFIER, value);
    }

    /**
     * Sets the {@link AttributeType#ATTACK_SPEED} value for this attribute.
     *
     * @param value - New value.
     */
    public void setAttackSpeed(double value) {
        setValueScaled(AttributeType.ATTACK_SPEED, value);
    }

    /**
     * Sets the {@link AttributeType#ATTACK_SPEED} to be "infinite".
     * <p>
     * This will allow spamming the weapon like in 1.8 and will remove the indicator.
     */
    public void setInfiniteAttackSpeed() {
        set(AttributeType.ATTACK_SPEED, INFINITE_ATTACK_SPEED);
    }

    /**
     * Sets the {@link AttributeType#KNOCKBACK_RESISTANCE} value for this attribute.
     *
     * @param value - New value.
     */
    public void setKnockbackResistance(double value) {
        setValueScaled(AttributeType.KNOCKBACK_RESISTANCE, value);
    }

    public void setEffectResistance(double value) {
        setValueScaled(AttributeType.EFFECT_RESISTANCE, value);
    }

    /**
     * Gets or computes the value into the map.
     *
     * @param type - Type.
     * @return the value.
     */
    public double get(@Nonnull AttributeType type) {
        return Math.min(mapped.computeIfAbsent(type, t -> 0.0d), type.maxValue());
    }

    /**
     * Sets a new value to an attribute.
     *
     * @param type  - Attribute type.
     * @param value - New value.
     */
    public void set(@Nonnull AttributeType type, double value) {
        mapped.put(type, value);
    }

    public final void reset() {
        mapped.clear();
    }

    /**
     * Removes the value.
     * <p>
     * If {@link #get(AttributeType)} is called, it will compute the value to 0, NOT default.
     *
     * @param type - Type.
     */
    public final void reset(@Nonnull AttributeType type) {
        mapped.remove(type);
    }

    public void forEach(@Nonnull BiConsumer<AttributeType, Double> consumer) {
        for (AttributeType type : AttributeType.values()) { // use values to keep sorted
            consumer.accept(type, get(type));
        }
    }

    public void forEachNonZero(@Nonnull BiConsumer<AttributeType, Double> consumer) {
        forEach((type, value) -> {
            if (value > 0.0d) {
                consumer.accept(type, value);
            }
        });
    }

    public void forEachMandatoryAndNonDefault(@Nonnull BiConsumer<AttributeType, Double> consumer) {
        forEach((type, value) -> {
            final double defaultValue = type.getDefaultValue();
            if (type.isMandatory() || value != defaultValue) {
                consumer.accept(type, value);
            }
        });
    }

    @Nonnull
    public String getLore(AttributeType type) {
        return " &7" + type.getName() + ": " + getStar(type);
    }

    @Nonnull
    public String getLore(@Nonnull Language language, @Nonnull AttributeType type) {
        return " &7" + language.getTranslated("attribute." + type.name().toLowerCase()) + ": " + getStar(type);
    }

    @Nonnull
    public String getStar(AttributeType type) {
        final double defaultValue = type.getDefaultValue();
        final double value = get(type);

        int scale = 1;
        final double d = value / defaultValue;

        if (d >= 0.75d && d < 1.0d) {
            scale = 2;
        }
        else if (d == 1.0d) {
            scale = 3;
        }
        else if (d > 1.0 && d <= 1.25d) {
            scale = 4;
        }
        else if (d > 1.25d && d <= 1.5d) {
            scale = 5;
        }

        final String character = type.attribute.getCharacter();
        final ChatColor color = type.attribute.getColor();

        return (color + character.repeat(scale)) + (ChatColor.DARK_GRAY + character.repeat(5 - scale));
    }

    @Nonnull
    @Override
    public Attributes weakCopy() {
        final Attributes copy = new Attributes();
        copy.mapped.putAll(mapped);

        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");

        int i = 0;
        for (AttributeType type : AttributeType.values()) {
            if (i++ != 0) {
                builder.append("\n");
            }

            final double v = get(type);

            if (v > 0) {
                builder.append(" ").append(type.name()).append(" = ").append(v);
            }
        }

        return builder.append("}").toString();
    }

    private void setValueScaled(AttributeType type, double value) {
        set(type, type.scaleDown(value));
    }
}
