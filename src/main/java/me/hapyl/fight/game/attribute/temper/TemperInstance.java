package me.hapyl.fight.game.attribute.temper;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.BuffDisplay;

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
 * For single attribute tempering, prefer {@link Temper#temper(LivingGameEntity, AttributeType, double, int, LivingGameEntity)}.
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

    public TemperInstance message(@Nonnull String message) {
        this.message = message;
        return this;
    }

    public TemperInstance onApply(@Nonnull Consumer<LivingGameEntity> onApply) {
        this.onApply = onApply;
        return this;
    }

    public double get(@Nonnull AttributeType type) {
        return values.getOrDefault(type, 0.0d);
    }

    public double getScaled(@Nonnull AttributeType type) {
        return type.scaleDown(get(type));
    }

    /**
     * @deprecated prefer providing applier {@link #temper(LivingGameEntity, int, LivingGameEntity)}
     */
    @Deprecated
    public final void temper(@Nonnull LivingGameEntity entity, int duration) {
        temper(entity, duration, null);
    }

    public final void temper(@Nonnull LivingGameEntity entity, int duration, @Nullable LivingGameEntity applier) {
        final EntityAttributes attributes = entity.getAttributes();
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

    public boolean isTempered(@Nonnull LivingGameEntity entity) {
        return entity.getAttributes().hasTemper(temper);
    }

    private <T> void ifNotNull(@Nullable T t, Consumer<T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
    }
}
