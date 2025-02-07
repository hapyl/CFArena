package me.hapyl.fight.game.heroes.ultimate;

import me.hapyl.fight.game.Response;

import javax.annotation.Nonnull;

/**
 * Represent an active ultimate instance.
 */
public abstract class UltimateInstance {

    private boolean forceEndUltimate;

    /**
     * Gets the response of this instance, use {@link UltimateTalent#error(String)}.
     */
    @Nonnull
    public Response response() {
        return Response.ok();
    }

    public void onCastStart() {
    }

    public void onCastTick(int tick) {
    }

    public void onCastEnd() {
    }

    public abstract void onExecute();

    public void onTick(int tick) {
    }

    public void onEnd() {
    }

    protected boolean isForceEndUltimate() {
        return forceEndUltimate;
    }

    protected void forceEndUltimate() {
        this.forceEndUltimate = true;
    }

}
