package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

public class Attributes {

    private final static double DEFENSE_SCALING = 0.75d;

    protected final Map<AttributeType, Double> mapped;
    private final AttributeRandom random;

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

    public final double calculateHealing(double healing) {
        return healing * get(AttributeType.MENDING);
    }

    public final double calculateIncomingDamage(double damage) {
        final double defense = get(AttributeType.DEFENSE);

        return damage / (defense * DEFENSE_SCALING + (1 - DEFENSE_SCALING));
    }

    public final CriticalResponse calculateOutgoingDamage(double damage) {
        return calculateOutgoingDamage(damage, null);
    }

    public final CriticalResponse calculateOutgoingDamage(double damage, @Nullable EnumDamageCause cause) {
        final double scaled = damage * AttributeType.ATTACK.get(this);

        if (cause != null && !cause.isCanCrit()) {
            return new CriticalResponse(scaled, false);
        }

        final boolean isCritical = isCritical();
        final double scaledCritical = scaleCritical(scaled, isCritical);

        return new CriticalResponse(scaledCritical, isCritical);
    }

    public final boolean calculateDodge() {
        return random.checkBound(AttributeType.DODGE);
    }

    public final boolean isCritical() {
        return random.checkBound(AttributeType.CRIT_CHANCE);
    }

    public final double scaleCritical(double damage, boolean isCritical) {
        return isCritical ? damage + (damage * AttributeType.CRIT_DAMAGE.get(this)) : damage;
    }

    public void setAttack(double value) {
        setValueScaled(AttributeType.ATTACK, value);
    }

    public void setDefense(double value) {
        setValueScaled(AttributeType.DEFENSE, value);
    }

    public void setSpeed(double value) {
        setValue(AttributeType.SPEED, value * 0.002d);
    }

    public void setValue(AttributeType type, double value) {
        mapped.put(type, value);
    }

    public void setValueScaled(AttributeType type, double value) {
        setValue(type, value / 100);
    }

    /**
     * Gets or computes the value into the map.
     *
     * @param type - Type.
     * @return the value.
     */
    public double get(AttributeType type) {
        return Math.min(mapped.computeIfAbsent(type, t -> 0.0d), type.maxValue());
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
    public final void reset(AttributeType type) {
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

    @Nonnull
    public String getLore(AttributeType type) {
        return " &7" + type.getName() + ": " + getStar(type);
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

    public double getHealth() {
        return get(AttributeType.MAX_HEALTH);
    }

    public void setHealth(double value) {
        setValue(AttributeType.MAX_HEALTH, value);
    }

    private void checkValue(double value) {
        if (value < 1.0) {
            throw new IllegalArgumentException("This method scales down the value! %s is too small, did you mean %s?".formatted(
                    value,
                    value * 100
            ));
        }
    }

    public static Attributes copyOf(@Nonnull Attributes attributes) {
        final Attributes copy = new Attributes();
        copy.mapped.putAll(attributes.mapped);

        return copy;
    }
}
