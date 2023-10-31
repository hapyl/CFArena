package me.hapyl.fight.game.attribute;

import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.temper.AttributeTemperTable;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.AttributeDisplay;
import me.hapyl.fight.trigger.Triggers;
import me.hapyl.fight.trigger.subscribe.AttributeChangeTrigger;
import me.hapyl.fight.util.collection.ImmutableTuple;
import me.hapyl.spigotutils.module.annotate.Super;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class stores player attributes that are changeable
 * during the game.
 * <p>
 * The stats itself defaults to 0, since they're considered
 * as additional stats and the getter returns the base value
 * plus additional.
 */
public class EntityAttributes extends Attributes implements PlayerElement {

    private final LivingGameEntity gameEntity;
    private final Attributes baseAttributes;

    private final AttributeTemperTable tempers;

    public EntityAttributes(LivingGameEntity gameEntity, Attributes baseAttributes) {
        this.gameEntity = gameEntity;
        this.baseAttributes = Attributes.copyOf(baseAttributes);
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
    public double get(AttributeType type) {
        return Math.min(getBase(type) + super.get(type) + tempers.get(type), type.maxValue());
    }

    public final int getFerocityStrikes() {
        double ferocity = get(AttributeType.FEROCITY);
        int strikes = 0;

        while (ferocity >= 1) {
            strikes += 1;
            ferocity -= 1;
        }

        if (ferocity > 0.0d) {
            if (new Random().nextDouble(0.0d, 1.0d) < ferocity) {
                strikes++;
            }
        }

        return strikes;
    }

    /**
     * Increases an attribute <b>temporary</b>.
     *
     * @param temper   - Temper of the increase.
     * @param type     - Type to attribute to increase.
     *                 If temper already affects attributes, the new temper will
     *                 override the old temper.
     * @param value    - Increase amount.
     * @param duration - Duration of increase.
     */
    public void increaseTemporary(@Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration) {
        final boolean newTemper = !tempers.has(temper);
        tempers.add(temper, type, value, duration);

        // do not spawn if player already has this temper
        if (newTemper) {
            display(type, value > -value);
        }

        type.attribute.update(gameEntity, get(type));
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

    /**
     * Computes an addition to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Add amount.
     * @return the new value.
     */
    public double add(@Nonnull AttributeType type, double value) {
        final ImmutableTuple<Double, Double> tuple = addSilent(type, value);
        final double oldValue = tuple.getA();
        final double newValue = tuple.getB();

        display(type, newValue > oldValue);
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
    public ImmutableTuple<Double, Double> addSilent(@Nonnull AttributeType type, double value) {
        final double oldBaseValue = get(type);
        final double original = super.get(type);
        final double newValue = original + value;

        mapped.put(type, newValue);

        // Call trigger
        final double newBaseValue = get(type);
        Triggers.call(new AttributeChangeTrigger(gameEntity, type, oldBaseValue, newBaseValue));

        // Call update
        type.attribute.update(gameEntity, newBaseValue);

        return ImmutableTuple.of(original, newValue);
    }

    /**
     * Computes a subtraction to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Subtract value.
     * @return the new value.
     */
    public double subtract(AttributeType type, double value) {
        return add(type, -value);
    }

    /**
     * Computes a subtraction to an attribute without displaying the change.
     *
     * @param type  - Type of attribute.
     * @param value - Subtract value.
     * @return the new value.
     */
    public double subtractSilent(AttributeType type, double value) {
        return addSilent(type, -value).b();
    }

    /**
     * Sets a new value to an attribute.
     *
     * @param type  - Attribute type.
     * @param value - New value.
     */
    public void set(AttributeType type, double value) {
        mapped.put(type, value);
    }

    /**
     * Returns the <b>base</b> <code>(unmodified)</code> value of this type.
     *
     * @param type - Type.
     * @return the base value of this type.
     */
    public double getBase(AttributeType type) {
        return baseAttributes.get(type);
    }

    /**
     * Gets the game player.
     *
     * @return the game player.
     */
    @Nonnull
    public LivingGameEntity getGameEntity() {
        return gameEntity;
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

    private void display(AttributeType type, boolean isBuff) {
        new AttributeDisplay(type, isBuff, gameEntity.getLocation().add(0.0d, 0.5d, 0.0d));
    }

}
