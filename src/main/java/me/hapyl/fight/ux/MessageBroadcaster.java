package me.hapyl.fight.ux;

import javax.annotation.Nullable;

public interface MessageBroadcaster {

    void broadcast(@Nullable Object... format);

}