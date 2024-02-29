package me.hapyl.fight.game.talents.archive.vortex;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;

import javax.annotation.Nonnull;

public abstract class AstralTask extends TickingGameTask {

    protected final GamePlayer player;
    protected final AstralStar star;

    protected boolean cancel;

    protected AstralTask(GamePlayer player, AstralStar star) {
        this.player = player;
        this.star = star;

        runTaskTimer(0, 1);
    }

    public abstract void run(@Nonnull GamePlayer player, @Nonnull AstralStar star, int tick);

    @Override
    public final void run(int tick) {
        if (player.isDeadOrRespawning()) {
            cancel();
            onCancel(CancelReason.PLAYER_DIED);
            return;
        }

        if (star.isDead()) {
            cancel();
            onCancel(CancelReason.STAR_DIED);
            return;
        }

        run(player, star, tick);
    }

    @Event
    protected void onCancel(@Nonnull CancelReason reason) {
    }

    protected enum CancelReason {
        PLAYER_DIED,
        STAR_DIED
    }

}
