package me.hapyl.fight.game.heroes.archive.juju;

import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

public class ArrowData extends GameTask {

    private final JuJu juju = Heroes.JUJU.getHero(JuJu.class);

    public final Player player;
    public final ArrowType type;

    public ArrowData(Player player, ArrowType type, int duration) {
        this.player = player;
        this.type = type;

        runTaskLater(duration);
    }

    @Override
    public void run() {
        juju.unequipArrow(player, type);
    }
}
