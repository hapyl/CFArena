package me.hapyl.fight.game.attribute;

public class LowAttributes extends Attributes {

    public LowAttributes() {
        setMaxHealth(20);
        setDefense(100);
        setAttack(100);
        set(AttributeType.CRIT_CHANCE, 0);
    }

}
