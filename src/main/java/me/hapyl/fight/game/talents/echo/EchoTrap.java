package me.hapyl.fight.game.talents.echo;

import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

public class EchoTrap {

    private final GamePlayer player;
    private final Location location;
    private final Direction direction;

    public EchoTrap(GamePlayer player, Location location, Direction direction) {
        this.player = player;
        this.location = location;
        this.direction = direction;
    }
}
