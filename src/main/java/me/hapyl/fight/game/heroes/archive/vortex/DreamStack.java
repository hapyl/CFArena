package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.Cancellable;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;

public class DreamStack implements Cancellable {

    private static final int STACK_DURATION = 100;

    private final GamePlayer player;
    private final PlayerMap<DreamStack> owningMap;
    protected int stacks;
    private GameTask task;

    public DreamStack(GamePlayer player, PlayerMap<DreamStack> owningMap) {
        this.player = player;
        this.owningMap = owningMap;
        this.stacks = 1;

        reschedule();
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

    public DreamStack increment() {
        stacks++;
        return this;
    }

    private void reschedule() {
        if (task != null) {
            task.cancel();
        }

        task = new GameTask() {
            @Override
            public void run() {
                if (--stacks > 0) {
                    reschedule();
                }
                else {
                    owningMap.remove(player);
                }
            }
        }.runTaskLater(STACK_DURATION);
    }
}
