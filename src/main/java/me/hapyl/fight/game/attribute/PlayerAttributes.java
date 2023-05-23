package me.hapyl.fight.game.attribute;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.task.CachedDelayedGameTaskList;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * This class stores player attributes that are changeable
 * during the game.
 * <p>
 * The stats itself defaults to 0, since they're considered
 * as additional stats and the getter returns the base value
 * plus additional.
 */
public class PlayerAttributes extends Attributes implements PlayerElement {

    private final GamePlayer gamePlayer;
    private final HeroAttributes heroAttributes;

    private final CachedDelayedGameTaskList tasks;

    public PlayerAttributes(GamePlayer gamePlayer, HeroAttributes heroAttributes) {
        this.gamePlayer = gamePlayer;
        this.heroAttributes = heroAttributes;
        this.tasks = new CachedDelayedGameTaskList();

        mapped.clear(); // default to 0
    }

    @Override
    public void onDeath(Player player) {
        tasks.cancelAll();
    }

    /**
     * Returns the base value plus the additional value.
     *
     * @param type - Type.
     * @return the base value plus the additional value.
     */
    @Override
    public double get(AttributeType type) {
        return getBase(type) + super.get(type);
    }

    /**
     * Increases an attribute <b>temporary</b>.
     *
     * @param type     - Type to attribute to increase.
     * @param value    - Increase amount.
     * @param duration - Duration of increase.
     * @return the new value.
     */
    public double increase(AttributeType type, double value, int duration) {
        final double added = add(type, value);

        if (duration < 0) {
            return added;
        }

        tasks.schedule(task -> subtract(type, value), duration);
        return added;
    }

    /**
     * Decreases an attribute <b>temporary</b>.
     *
     * @param type     - Type of attribute to decrease.
     * @param value    - Decrease amount.
     * @param duration - Duration of decrease.
     * @return the new value.
     */
    public double decrease(AttributeType type, double value, int duration) {
        return increase(type, -value, duration);
    }

    /**
     * Computes an addition to an attribute.
     *
     * @param type  - Type of attribute.
     * @param value - Add amount.
     * @return the new value.
     */
    public double add(AttributeType type, double value) {
        return mapped.compute(type, (t, v) -> (v == null ? 0 : v) + value);
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
     * Sets a new value to an attribute.
     *
     * @param type  - Attribute type.
     * @param value - New value.
     */
    public void set(AttributeType type, double value) {
        mapped.put(type, value + getBase(type));
    }

    /**
     * Returns the <b>base</b> <code>(unmodified)</code> value of this type.
     *
     * @param type - Type.
     * @return the base value of this type.
     */
    public double getBase(AttributeType type) {
        return heroAttributes.get(type);
    }

    /**
     * Gets the game player.
     *
     * @return the game player.
     */
    @Nonnull
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    /**
     * Gets the base attributes.
     *
     * @return the base attributes.
     */
    public HeroAttributes getBaseAttributes() {
        return heroAttributes;
    }

}
