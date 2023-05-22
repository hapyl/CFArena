package me.hapyl.fight.game.attribute;

import me.hapyl.fight.game.GamePlayer;

/**
 * This class stores player attributes that are changeable
 * during the game.
 * <p>
 * The stats itself defaults to 0, since they're considered
 * as additional stats and the getter returns the base value
 * plus additional.
 */
public class PlayerAttributes extends Attributes {

    private final GamePlayer gamePlayer;
    private final HeroAttributes heroAttributes;

    public PlayerAttributes(GamePlayer gamePlayer, HeroAttributes heroAttributes) {
        this.gamePlayer = gamePlayer;
        this.heroAttributes = heroAttributes;

        mapped.clear(); // default to 0
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

    public double add(AttributeType type, double value) {
        return mapped.compute(type, (t, v) -> (v == null ? 0 : v) + value);
    }

    public void set(AttributeType type, double value) {
        mapped.put(type, value + getBase(type));
    }

    public double subtract(AttributeType type, double value) {
        return add(type, -value);
    }

    /**
     * Returns the base value of this type.
     *
     * @param type - Type.
     * @return the base value of this type.
     */
    public double getBase(AttributeType type) {
        return heroAttributes.get(type);
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public HeroAttributes getHeroAttributes() {
        return heroAttributes;
    }
}
