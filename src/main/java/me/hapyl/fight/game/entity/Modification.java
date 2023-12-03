package me.hapyl.fight.game.entity;

public interface Modification<T> {

    void andThen(T t);

}
