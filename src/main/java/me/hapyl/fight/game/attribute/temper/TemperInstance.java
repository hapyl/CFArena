package me.hapyl.fight.game.attribute.temper;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.eterna.module.util.BFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Temper Instance is used to statically store the
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
    private Consumer<LivingGameEntity> onApply;

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
        return increase(type, type.scaleDown(value));
    }

    public TemperInstance decreaseScaled(@Nonnull AttributeType type, double value) {
        return decrease(type, type.scaleDown(value));
    }

    public TemperInstance message(@Nonnull String message, @Nullable Object... format) {
        this.message = BFormat.format(message, format);
        return this;
    }

    public TemperInstance onApply(@Nonnull Consumer<LivingGameEntity> onApply) {
        this.onApply = onApply;
        return this;
    }

    /**
     * Tempers {@link LivingGameEntity} attributes with this instance for the given duration.
     *
     * @param entity   - Entity.
     * @param duration - Duration.
     */
    public final void temper(@Nonnull LivingGameEntity entity, int duration) {
        temper(entity.getAttributes(), duration);
    }

    /**
     * Tempers {@link LivingGameEntity} attributes with this instance for indefinite duration.
     * <p>
     * <b>Note</b>
     * Event though {@link Temper} was designed as a duration-based attribute increase, they do support infinite duration.
     *
     * @param entity - Entity.
     */
    public final void temper(@Nonnull LivingGameEntity entity) {
        temper(entity, -1);
    }

    public double get(@Nonnull AttributeType type) {
        return values.getOrDefault(type, 0.0d);
    }

    public double getScaled(@Nonnull AttributeType type) {
        return type.scaleDown(get(type));
    }

    public final void temper(@Nonnull EntityAttributes attributes, int duration) {
        temper(attributes, duration, null);
    }

    public final void temper(@Nonnull EntityAttributes attributes, int duration, @Nullable LivingGameEntity applier) {
        final LivingGameEntity entity = attributes.getEntity();
        final boolean newTemper = !attributes.hasTemper(temper);

        values.forEach((t, v) -> attributes.increaseTemporary(temper, t, v, duration, true, applier));

        // Fx
        if (newTemper) {
            ifNotNull(name, then -> entity.spawnDisplay(then, 20, BuffDisplay::new));
            ifNotNull(message, entity::sendMessage);
            ifNotNull(onApply, then -> then.accept(entity));
        }
    }

    public void untemper(LivingGameEntity entity) {
        entity.getAttributes().resetTemper(temper);
    }

    private <T> void ifNotNull(@Nullable T t, Consumer<T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
    }
}
