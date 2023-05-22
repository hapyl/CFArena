package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;

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
        return damage / get(AttributeType.DEFENSE);
    }

    public CriticalResponse calculateOutgoingDamage(double damage) {
        final double scaled = damage * AttributeType.ATTACK.get(this);

        final boolean isCritical = isCritical();
        final double scaledCritical = scaleCritical(scaled, isCritical);

        return new CriticalResponse(scaledCritical, isCritical);
    }

    public double scaleCritical(double damage, boolean isCritical) {
        return isCritical ? damage + (damage * AttributeType.CRIT_DAMAGE.get(this)) : damage;
    }

    public boolean isCritical() {
        return new Random().nextDouble(0.0d, 1.0d) < AttributeType.CRIT_CHANCE.get(this);
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

    public void forEach(BiConsumer<AttributeType, Double> consumer) {
        for (AttributeType type : mapped.keySet()) {
            consumer.accept(type, get(type));
        }
    }
}
