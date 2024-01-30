package me.hapyl.fight.exception;

public class HandleNotSetException extends NullPointerException {

    public HandleNotSetException(Enum<?> anEnum) {
        super("Handle not set for " + anEnum.name() + "!");
    }

}
