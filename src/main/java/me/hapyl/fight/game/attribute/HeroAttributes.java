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

    public void setValue(AttributeType type, double value) {
        mapped.put(type, value);
    }
}
