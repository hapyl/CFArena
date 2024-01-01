package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface ErrorHandlingRunnable extends Runnable {

    default void handle(@Nonnull Exception exception) {
    }

}
