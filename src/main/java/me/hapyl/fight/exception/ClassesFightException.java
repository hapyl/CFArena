package me.hapyl.fight.exception;

import me.hapyl.spigotutils.module.chat.Chat;

public class ClassesFightException extends RuntimeException {
    public ClassesFightException(String message) {
        final RuntimeException exception = new RuntimeException(message);

        Chat.broadcastOp("&4&lException! &c" + message);
        final StackTraceElement[] trace = exception.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            if (i > 5) {
                break;
            }

            Chat.broadcastOp("&e " + trace[i]);
        }

        Chat.broadcastOp("&cSee console for details!");
        exception.printStackTrace();
    }
}
