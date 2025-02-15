package me.hapyl.fight.game.attribute;

public class LowAttributes extends BaseAttributes {

    public LowAttributes() {
        setMaxHealth(20);
        setDefense(100);
        setAttack(100);
        set(AttributeType.CRIT_CHANCE, 0);
    }

}
