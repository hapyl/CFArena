package me.hapyl.fight.game.heroes;

public class GenericEnum<E> {

    protected final String name;
    protected final E value;

    public GenericEnum(E value) {
        this.value = value;
        this.name = name();
    }

    public E getValue() {
        return value;
    }

    public String name() {
        return name;
    }

}
