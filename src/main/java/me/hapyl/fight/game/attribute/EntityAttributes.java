package me.hapyl.fight.game.attribute;

import me.hapyl.fight.annotate.Trigger;
import me.hapyl.fight.event.custom.AttributeChangeEvent;
import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.temper.AttributeTemperTable;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperData;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.BuffDisplay;
import me.hapyl.fight.game.ui.display.DebuffDisplay;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.trigger.Triggers;
import me.hapyl.fight.trigger.subscribe.AttributeChangeTrigger;
import me.hapyl.fight.util.collection.NonnullTuple;
import me.hapyl.fight.util.collection.Tuple;
import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.math.Numbers;
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
public class EntityAttributes extends Attributes implements PlayerElement {

    protected final AttributeTemperTable tempers;

    private final LivingGameEntity entity;
    private final Attributes baseAttributes;

    public EntityAttributes(LivingGameEntity entity, Attributes baseAttributes) {
        this.entity = entity;
        this.baseAttributes = baseAttributes.weakCopy();
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
        return Numbers.clamp(
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
        double ferocity = get(AttributeType.FEROCITY);
        int strikes = 0;

        while (ferocity >= 1) {
            strikes += 1;
            ferocity -= 1;
        }

        if (ferocity > 0.0d) {
            if (random.checkBound(ferocity)) {
                strikes++;
            }
        }

        return strikes;
    }

    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        increaseTemporary(temper, type, value, duration, false, null);
    }

    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, @Nullable LivingGameEntity applier) {
        increaseTemporary(temper, type, value, duration, false, applier);
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
    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, boolean silent, @Nullable LivingGameEntity applier) {
        if (new AttributeTemperEvent(entity, temper, type, value, duration, silent).callAndCheck()) {
            return;
        }

        final boolean newTemper = !tempers.has(temper, type);
        final boolean isBuff = type.getDisplayType(value, -value);

        tempers.add(temper, type, value, duration, isBuff, applier);

        // do not spawn if player already has this temper
        if (!silent && temper.isDisplay() && newTemper) {
            display(type, isBuff);
        }

        triggerUpdate(type);
    }

    /**
     * Decreases an attribute <b>temporary</b>.
     *
     * @param temper   - Temper of the decrease.
     * @param type     - Type of attribute to decrease.
     * @param value    - Decrease amount.
     * @param duration - Duration of decrease.
     */
    public void decreaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        increaseTemporary(temper, type, -value, duration);
    }

    public void decreaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, @Nullable LivingGameEntity applier) {
        increaseTemporary(temper, type, -value, duration, applier);
    }

    /**
     * Computes an addition to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Add amount.
     * @return the new value.
     */
    public double add(@Nonnull AttributeType type, double value) {
        final NonnullTuple<Double, Double> tuple = addSilent(type, value);
        final double oldValue = tuple.getA();
        final double newValue = tuple.getB();

        display(type, type.getDisplayType(newValue, oldValue));
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
    public NonnullTuple<Double, Double> addSilent(@Nonnull AttributeType type, double value) {
        final double oldBaseValue = get(type);
        final double original = super.get(type);
        final double newValue = original + value;

        // Call event
        if (new AttributeChangeEvent(entity, type, original, newValue).callAndCheck()) {
            return Tuple.ofNonnull(0.d, 0.d);
        }

        mapped.put(type, newValue);

        // Call trigger
        final double newBaseValue = get(type);
        Triggers.call(new AttributeChangeTrigger(entity, type, oldBaseValue, newBaseValue));

        // Call update
        triggerUpdate(type);

        return Tuple.ofNonnull(original, newValue);
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
    public Attributes getBaseAttributes() {
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

    @Nonnull
    @Override
    public WeakEntityAttributes weakCopy() {
        return new WeakEntityAttributes(this);
    }

    private void display(AttributeType type, boolean isBuff) {
        final Location location = entity.getMidpointLocation();

        final StringDisplay display = isBuff
                ? new BuffDisplay("&a&l▲ %s &a&l▲".formatted(type), 30)
                : new DebuffDisplay("&c&l▼ %s &c&l▼".formatted(type), 30);

        display.display(location);
    }


    @Override
    public String toString() {
        return entity.toString() + super.toString();
    }
}
