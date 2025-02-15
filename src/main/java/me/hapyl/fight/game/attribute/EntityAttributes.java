package me.hapyl.fight.game.attribute;

import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.annotate.Trigger;
import me.hapyl.fight.event.custom.AttributeChangeEvent;
import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.game.attribute.temper.AttributeTemperTable;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperData;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import me.hapyl.fight.game.ui.display.StringDisplay;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class stores entity attributes that are changeable
 * during the game.
 * <p>
 * The stats itself defaults to 0, since they're considered
 * as additional stats and the getter returns the base value
 * plus additional.
 *
 * @see #get(AttributeType)
 */
public class EntityAttributes extends BaseAttributes implements PlayerElementHandler {

    protected final AttributeTemperTable tempers;

    private final LivingGameEntity entity;
    private final BaseAttributes baseAttributes;

    public EntityAttributes(LivingGameEntity entity, BaseAttributes baseAttributes) {
        this.entity = entity;
        this.baseAttributes = baseAttributes.createCopy();
        this.tempers = new AttributeTemperTable(this);

        mapped.clear(); // default to 0
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        tempers.cancelAll();
    }

    /**
     * Returns the base value plus the additional value.
     *
     * @param type - Type.
     * @return the base value plus the additional value.
     */
    @Override
    public double get(@Nonnull AttributeType type) {
        return Math.clamp(
                getBase(type) + super.get(type) + tempers.get(type),
                type.minValue(),
                type.maxValue()
        );
    }

    @Override
    public void set(@Nonnull AttributeType type, double value) {
        super.set(type, value);

        triggerUpdate(type);
    }

    @Trigger
    public void triggerUpdate(@Nonnull AttributeType type) {
        type.attribute.update(entity, get(type));
    }

    public final int getFerocityStrikes() {
        final double ferocity = get(AttributeType.FEROCITY);

        int strikes = (int) ferocity;
        double remainder = ferocity % 1;

        if (remainder > 0.0d) {
            if (random.checkBound(remainder)) {
                strikes++;
            }
        }

        return strikes;
    }

    /**
     * @deprecated prefer providing applier {@link #increaseTemporary(Temper, AttributeType, double, int, LivingGameEntity)}
     */
    @Deprecated
    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        increaseTemporary(temper, type, value, duration, false, null);
    }

    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, @Nullable LivingGameEntity applier) {
        increaseTemporary(temper, type, value, duration, false, applier);
    }

    /**
     * @deprecated prefer providing applier {@link #decreaseTemporary(Temper, AttributeType, double, int, LivingGameEntity)}
     */
    @Deprecated
    public void decreaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        increaseTemporary(temper, type, -value, duration);
    }

    public void decreaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, @Nullable LivingGameEntity applier) {
        increaseTemporary(temper, type, -value, duration, applier);
    }

    /**
     * Increases an {@link AttributeType} temporary.
     *
     * @param temper   - Temper.
     * @param type     - Attribute type.
     * @param value    - Value.
     * @param duration - Duration.
     * @param silent   - Should be silent.
     * @param applier  - Who increased the attribute.
     *                 <b>This delegates to whoever applied the attribute, like a teammate who buffed or an enemy who debuffed!</b>
     */
    @Super
    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, boolean silent, @Nullable LivingGameEntity applier) {
        if (new AttributeTemperEvent(entity, temper, type, value, duration, silent, applier).call()) {
            return;
        }

        final boolean newTemper = !tempers.has(temper, type);
        final boolean isBuff = type.isBuff(value, -value);

        tempers.add(temper, type, value, duration, isBuff, applier);

        // do not spawn if player already has this temper
        if (!silent && temper.isDisplay() && newTemper) {
            display(type, isBuff);
        }

        triggerUpdate(type);
    }

    /**
     * Computes an addition to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Add amount.
     * @return the new value.
     */
    public double add(@Nonnull AttributeType type, double value) {
        final Tuple<Double, Double> tuple = addSilent(type, value);
        final double oldValue = tuple.getA();
        final double newValue = tuple.getB();

        display(type, type.isBuff(newValue, oldValue));
        return oldValue;
    }

    /**
     * Computes an addition to an attribute without displaying the change.
     *
     * @param type  - Type of attribute.
     * @param value - Add amount.
     * @return the new value.
     */
    @Super
    public Tuple<Double, Double> addSilent(@Nonnull AttributeType type, double value) {
        final double original = super.get(type);
        final double newValue = original + value;

        // Call event
        if (new AttributeChangeEvent(entity, type, original, newValue).call()) {
            return Tuple.of(0.d, 0.d);
        }

        mapped.put(type, newValue);

        // Call update
        triggerUpdate(type);

        return Tuple.of(original, newValue);
    }

    /**
     * Computes a subtraction to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Subtract value.
     * @return the new value.
     */
    public double subtract(@Nonnull AttributeType type, double value) {
        return add(type, -value);
    }

    /**
     * Computes a subtraction to an attribute without displaying the change.
     *
     * @param type  - Type of attribute.
     * @param value - Subtract value.
     * @return the new value.
     */
    public double subtractSilent(@Nonnull AttributeType type, double value) {
        return addSilent(type, -value).b();
    }

    /**
     * Returns the <b>base</b> <code>(unmodified)</code> value of this type.
     *
     * @param type - Type.
     * @return the base value of this type.
     */
    public double getBase(@Nonnull AttributeType type) {
        return baseAttributes.get(type);
    }

    /**
     * Gets the game player.
     *
     * @return the game player.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * Gets the base attributes.
     *
     * @return the base attributes.
     */
    @Nonnull
    public BaseAttributes getBaseAttributes() {
        return baseAttributes;
    }

    public boolean hasTemper(Temper temper) {
        return tempers.has(temper);
    }

    @Deprecated
    public double getRaw(AttributeType attributeType) {
        return super.get(attributeType);
    }

    public void forEachTempers(@Nonnull Consumer<TemperData> consumer) {
        tempers.forEach(consumer);
    }

    public boolean hasTempers() {
        return !tempers.isEmpty();
    }

    public void resetTemper(@Nonnull Temper temper) {
        tempers.cancel(temper);
    }

    @Override
    public String toString() {
        return entity.toString() + super.toString();
    }

    private void display(AttributeType type, boolean isBuff) {
        final Location location = entity.getMidpointLocation();

        final StringDisplay display = isBuff
                ? new BuffDisplay("&a&l▲ %s &a&l▲".formatted(type), 30)
                : new DebuffDisplay("&c&l▼ %s &c&l▼".formatted(type), 30);

        display.display(location);
    }
}
