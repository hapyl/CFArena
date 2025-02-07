package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.alchemist.AlchemistPotion;
import me.hapyl.fight.game.task.TickingGameTask;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class ActivePotion extends TickingGameTask {

    protected final AlchemistData data;
    protected final GamePlayer player;
    protected final AlchemistPotion potion;

    public ActivePotion(AlchemistData data, GamePlayer player, AlchemistPotion potion) {
        this.data = data;
        this.player = player;
        this.potion = potion;

        runTaskTimer(0, 1);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void run(int tick) {
        if (player.isDeadOrRespawning() || tick >= potion.getDuration()) {
            data.cancelActivePotion();
        }
    }
}
