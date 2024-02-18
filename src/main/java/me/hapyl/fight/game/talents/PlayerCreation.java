package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class PlayerCreation extends TickingGameTask implements Creation, TalentReference<CreationTalent> {

    protected final CreationTalent talent;
    protected final GamePlayer player;

    protected PlayerCreation(@Nonnull CreationTalent talent, @Nonnull GamePlayer player) {
        this.talent = talent;
        this.player = player;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void run(int tick) {
        if (shouldRemove()) {
            talent.removeCreation(player, this);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void remove() {
        cancel();
    }

    @Override
    public boolean shouldRemove() {
        return getTick() >= talent.getDuration();
    }

    @Nonnull
    @Override
    public CreationTalent getTalent() {
        return talent;
    }
}
