package me.hapyl.fight.game.talents.jester;

import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import org.bukkit.Location;

public class MusicBoxEntity implements Removable {

    private final GamePlayer player;
    private final Location location;
    private final DisplayEntity entity;

    public MusicBoxEntity(GamePlayer player, Location location, DisplayData displayData) {
        this.player = player;
        this.location = location;
        this.entity = displayData.spawn(location);
    }

    @Override
    public void remove() {
        entity.remove();
    }
}
