package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

public class Response {
    
    public static final Response OK = new Response("", Type.OK);
    public static final Response ERROR = new Response("", Type.ERROR);
    public static final Response AWAIT = new Response("", Type.AWAIT);
    
    public static final Response ERROR_DEFAULT = error("Incomplete talent, report this!");
    public static final Response DEPRECATED = error("Deprecated talent.");
    
    private final String reason;
    private final Type type;
    
    private Response(@Nonnull String reason, @Nonnull Type type) {
        this.reason = reason;
        this.type = type;
    }
    
    @Nonnull
    public String reason() {
        return reason;
    }
    
    @Nonnull
    public Type type() {
        return type;
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
    
    public void sendError(@Nonnull GamePlayer player) {
        if (isError() && !reason.isEmpty()) {
            player.sendMessage("&8[&c❌&8] &4" + reason);
        }
    }
    
    @Override
    public String toString() {
        return reason;
    }
    
    @Nonnull
    public static Response error(@Nonnull String reason) {
        return new Response(reason, Type.ERROR);
    }
    
    public static void error(@Nonnull GamePlayer player, @Nonnull String reason) {
        error(reason).sendError(player);
    }
    
    @Nonnull
    @ApiStatus.Obsolete // Just use static constant
    public static Response ok() {
        return OK;
    }
    
    @Nonnull
    public static Response await() {
        return AWAIT;
    }
    
    public enum Type {
        /**
         * An error has occurred.
         */
        ERROR,
        
        /**
         * Everything is ok.
         */
        OK,
        
        /**
         * Everything is ok, but don't start the cooldown.
         */
        AWAIT
    }
    
}
