package me.hapyl.fight.game.attribute;

import me.hapyl.fight.game.heroes.Hero;

public class HeroAttributes extends Attributes {

    private final Hero hero;

    public HeroAttributes(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHealth(double value) {
        setValue(AttributeType.HEALTH, value);
    }

    public void setAttack(double value) {
        setValueScaled(AttributeType.ATTACK, 100);
    }

    public void setDefense(double value) {
        checkValue(value);
    }

    public void setValue(AttributeType type, double value) {
        mapped.put(type, value);
    }

    public void setValueScaled(AttributeType type, double value) {
        setValue(type, value * 100);
    }

    private void checkValue(double value) {
        if (value < 1.0) {
            throw new IllegalArgumentException("This method scales down the value! %s is too small, did you mean %s?".formatted(
                    value,
                    value * 100
            ));
        }
    }

}
