package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

public class Attributes {

    protected final Map<AttributeType, Double> mapped = Maps.newHashMap();

    public Attributes() {
        // write defaults
        for (AttributeType value : AttributeType.values()) {
            mapped.put(value, value.getDefaultValue());
        }
    }

    public double calculateIncomingDamage(double damage) {
        double defense = get(AttributeType.DEFENSE);

        // should never happen but just in case defense is 0
        if (defense <= 0.0d) {
            defense = 0.1d;
        }

        return damage / defense;
    }

    public CriticalResponse calculateOutgoingDamage(double damage) {
        return calculateOutgoingDamage(damage, null);
    }

    public CriticalResponse calculateOutgoingDamage(double damage, @Nullable EnumDamageCause cause) {
        final double scaled = damage * AttributeType.ATTACK.get(this);

        if (cause != null && !cause.isCanCrit()) {
            return new CriticalResponse(scaled, false);
        }

        final boolean isCritical = isCritical();
        final double scaledCritical = scaleCritical(scaled, isCritical);

        return new CriticalResponse(scaledCritical, isCritical);
    }

    public double scaleCritical(double damage, boolean isCritical) {
        return isCritical ? damage + (damage * AttributeType.CRIT_DAMAGE.get(this)) : damage;
    }

    public boolean isCritical() {
        final double chance = AttributeType.CRIT_CHANCE.get(this);
        return chance >= 1.0d || new Random().nextDouble(0.0d, 1.0d) < chance;
    }

    /**
     * Gets or computes the value into the map.
     *
     * @param type - Type.
     * @return the value.
     */
    public double get(AttributeType type) {
        return mapped.computeIfAbsent(type, t -> 0.0d);
    }

    /**
     * Removes the value.
     * <p>
     * If {@link #get(AttributeType)} is called, it will compute the value to 0, NOT default.
     *
     * @param type - Type.
     * @return the old value.
     */
    public double remove(AttributeType type) {
        return mapped.remove(type);
    }

    public void reset() {
        mapped.clear();
    }

    public void reset(AttributeType type) {
        mapped.remove(type);
    }

    public void forEach(BiConsumer<AttributeType, Double> consumer) {
        for (AttributeType type : AttributeType.values()) { // use values to keep sorted
            consumer.accept(type, get(type));
        }
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
}
