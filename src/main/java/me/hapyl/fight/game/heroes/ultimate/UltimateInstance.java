package me.hapyl.fight.game.heroes.ultimate;

import me.hapyl.fight.annotate.BoolGuide;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represent an active ultimate instance.
 */
public abstract class UltimateInstance {
    
    @BoolGuide(
            whenNull = "Does nothing",
            whenTrue = "Ends ultimate and calls onEnd()",
            whenFalse = "Ends ultimate without calling onEnd()"
    )
    @Nullable
    protected Boolean forceEndUltimate;
    
    /**
     * Gets the response of this instance, use {@link UltimateTalent#error(String)}.
     */
    @Nonnull
    public Response response() {
        return Response.ok();
    }
    
    /**
     * Called at the first tick of the ultimate execution if, and only if, {@code castDuration > 0}.
     * <p>This method is completely ignored if ultimate has no cast duration.</p>
     */
    public void onCastStart() {
    }
    
    /**
     * Called every tick, from {@code 0-castDuration} (inclusive) if, and only if, {@code castDuration > 0}.
     * <p>This method is completely ignored if ultimate has no cast duration.</p>
     *
     * @param tick - The current tick, from {@code 0} to {@code castDuration} (inclusive).
     */
    public void onCastTick(int tick) {
    }
    
    /**
     * Called at the last tick ({@code castDuration}) if, and only if, {@code castDuration > 0}.
     * <p>This method is completely ignored if ultimate has no cast duration.</p>
     */
    public void onCastEnd() {
    }
    
    /**
     * Called at the execution state of the ultimate, which depends on the ultimate structure.
     * <p>For most ultimates, this method will be executed instantly, but if {@code castDuration > 0} then this method will only be called after the casting has ended.</p>
     */
    public abstract void onExecute();
    
    /**
     * Called every tick, from {@code 0-duration} (inclusive) if, and only if, {@code duration > 0}.
     * <p>This method is completely ignored if ultimate is instant cast (no duration).</p>
     *
     * @param tick - The current tick, from {@code 0} to {@code duration} (inclusive).
     */
    public void onTick(int tick) {
    }
    
    /**
     * Called whenever this ultimate has ended, be it right after execution if {@code duration == 0} or else at {@code tick == duration}.
     */
    public void onEnd() {
    }
    
    /**
     * Called when a player has died when using this ultimate while it's casting or ticking.
     *
     * @param player - The player who has died.
     */
    public void onPlayerDied(@Nonnull GamePlayer player) {
    }
    
    protected void forceEndUltimate(boolean callOnEnd) {
        this.forceEndUltimate = callOnEnd;
    }
    
}
