package me.hapyl.fight.game.talents.storage.tamer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a blueprint for a pack of entities.
 */
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
     * Ticks every tick, duh.
     *
     * @param player - Player who owns the pack
     * @param pack   - Pack that is being ticked.
     */
    public void onTick(Player player, TamerPack pack) {
    }

    /**
     * Called whenever player used their ultimate.
     *
     * @param player - Player who used the ultimate
     * @param pack   - Pack that was used with.
     */
    public void onUltimate(Player player, TamerPack pack) {
    }

}
