package me.hapyl.fight.game.attribute.temper;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.spigotutils.module.util.BFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Temper Instance if used to statically store the
 * temping values and applying them whenever.
 * <p>
 * <b>This is designed for multiple attribute tempering at once!</b>
 * <p>
 * For single attribute tempering, prefer {@link Temper#temper(LivingGameEntity, AttributeType, double, int)}.
 */
public class TemperInstance {

    private final Temper temper;
    private final String name;
    private final Map<AttributeType, Double> values;

    private String message;

    TemperInstance(@Nonnull Temper temper, @Nullable String name) {
        this.temper = temper;
        this.name = name;
        this.values = Maps.newHashMap();
    }

    public TemperInstance increase(@Nonnull AttributeType type, double value) {
        values.put(type, value);
        return this;
    }

    public TemperInstance decrease(@Nonnull AttributeType type, double value) {
        return increase(type, -value);
    }

    public TemperInstance increaseScaled(@Nonnull AttributeType type, double value) {
        return increase(type, type.scale(value));
    }

    public TemperInstance decreaseScaled(@Nonnull AttributeType type, double value) {
        return decrease(type, type.scale(value));
    }

    public TemperInstance message(@Nonnull String message, @Nullable Object... format) {
        this.message = BFormat.format(message, format);
        return this;
    }

    public final void temper(@Nonnull LivingGameEntity entity, int duration) {
        temper(entity.getAttributes(), duration);
    }

    public final void temper(@Nonnull EntityAttributes attributes, int duration) {
        final LivingGameEntity entity = attributes.getEntity();
        final boolean newTemper = !attributes.hasTemper(temper);

        values.forEach((t, v) -> attributes.increaseTemporary(temper, t, v, duration, true));

        // Fx
        if (newTemper) {
            ifNotNull(name, str -> entity.spawnDisplay(name, 20, BuffDisplay::new));
            ifNotNull(message, str -> entity.sendMessage(message));
        }
    }

    private void ifNotNull(String string, Consumer<String> consumer) {
        if (string != null) {
            consumer.accept(string);
        }
    }
}
