package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public class UltimateResponse {

    public static final UltimateResponse OK = new UltimateResponse();

    private final Type type;
    private final String reason;

    public UltimateResponse() {
        this(Type.OK, null);
    }

    private UltimateResponse(Type type, String reason) {
        this.type = type;
        this.reason = reason;
    }

    public boolean isOk() {
        return type == Type.OK;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    public String getReason() {
        return reason;
    }

    /**
     * Called after the cast duration is ended.
     *
     * @param player - Player.
     */
    @Event
    public void onCastFinished(@Nonnull GamePlayer player) {
    }

    /**
     * Called whenever ultimate ticks reach 0.
     * <h1>This is not called if ultimate has no duration!</h1>
     *
     * @param player - Player.
     */
    @Event
    public void onUltimateEnd(@Nonnull GamePlayer player) {
    }

    public static UltimateResponse error(@Nonnull String reason) {
        return new UltimateResponse(Type.ERROR, reason);
    }

    public enum Type {
        OK,
        ERROR
    }

}
