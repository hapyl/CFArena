package me.hapyl.fight.game.talents.storage.tamer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Pack {

    private final String name;

    public Pack(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int spawnAmount() {
        return 1;
    }

    public abstract void spawnEntity(Player player, Location location, TamerPack pack);

    /**
     * Ticks every tick.
     */
    public void onTick(Player player, TamerPack pack) {
    }

}
