package me.hapyl.fight.script;

import me.hapyl.spigotutils.module.chat.Chat;

public class ScriptException extends RuntimeException {

    public ScriptException(String message, Object... format) {
        super(message.formatted(format));

        Chat.broadcastOp("&4Script: &c" + message.formatted(format));
    }

}
