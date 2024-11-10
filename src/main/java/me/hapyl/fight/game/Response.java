package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Response {

    public static final Response OK = new Response(null, Type.OK);
    public static final Response ERROR = new Response(null, Type.ERROR);
    public static final Response AWAIT = new Response(null, Type.AWAIT);
    public static final Response ERROR_DEFAULT = error("Talent is not complete!");

    private final String reason;
    private final Type type;

    public Response(String reason, Type type) {
        this.reason = reason;
        this.type = type;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public boolean isOk() {
        return this.type == Type.OK;
    }

    public boolean isError() {
        return this.type == Type.ERROR;
    }

    public boolean isAwait() {
        return this.type == Type.AWAIT;
    }

    public void sendError(GamePlayer player) {
        if (isError() && reason != null) {
            player.sendMessage("&8[&c❌&8] &4" + reason);
        }
    }

    @Nonnull
    public static Response error(@Nonnull String reason) {
        return new Response(reason, Type.ERROR);
    }

    @Override
    public String toString() {
        return String.valueOf(reason);
    }

    @Nonnull
    public static Response ok() {
        return new Response(null, Type.OK);
    }

    @Nonnull
    public static Response await() {
        return new Response(null, Type.AWAIT);
    }

    public enum Type {
        ERROR,
        OK,
        AWAIT // basically ok but does not start cooldown
    }

}
