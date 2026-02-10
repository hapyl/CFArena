package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class AwaitedList<T> {

    private final LinkedList<T> list;
    private T awaiting;

    public AwaitedList() {
        this.list = new LinkedList<>();
    }

    public void acceptAdd() {
        if (awaiting == null) {
            return;
        }

        list.add(awaiting);
        awaiting = null;
    }

    public void acceptRemove() {
        if (awaiting == null) {
            return;
        }

        list.remove(awaiting);
        awaiting = null;
    }

    public void await(@Nonnull T t) {
        this.awaiting = t;
    }


}
