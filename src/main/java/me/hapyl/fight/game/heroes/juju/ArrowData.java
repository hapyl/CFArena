package me.hapyl.fight.game.heroes.juju;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.task.GameTask;

public class ArrowData extends GameTask {

    public final GamePlayer player;
    public final ArrowType type;

    public ArrowData(GamePlayer player, ArrowType type, int duration) {
        this.player = player;
        this.type = type;

        runTaskLater(duration);
    }

    @Override
    public void run() {
        HeroRegistry.JUJU.unequipArrow(player, type);
    }
}
