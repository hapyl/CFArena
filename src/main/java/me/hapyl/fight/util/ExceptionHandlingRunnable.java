package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface ExceptionHandlingRunnable extends Runnable {
    
    @Override
    void run();
    
    default void exception(@Nonnull Exception exception) {
    }
}
