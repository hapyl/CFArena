package me.hapyl.fight.game.heroes.juju;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

public class ArrowData extends GameTask {

    private final JuJu juju = Heroes.JUJU.getHero(JuJu.class);

    public final GamePlayer player;
    public final ArrowType type;

    public ArrowData(GamePlayer player, ArrowType type, int duration) {
        this.player = player;
        this.type = type;

        runTaskLater(duration);
    }

    @Override
    public void run() {
        juju.unequipArrow(player, type);
    }
}
