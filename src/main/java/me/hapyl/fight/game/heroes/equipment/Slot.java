package me.hapyl.fight.game.heroes.equipment;

public enum Slot {

    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    HAND,
    OFFHAND;

    public int getId() {
        return this.ordinal();
    }
}
