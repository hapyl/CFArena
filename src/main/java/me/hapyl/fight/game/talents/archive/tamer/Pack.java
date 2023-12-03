package me.hapyl.fight.game.talents.archive.tamer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a blueprint for a pack of entities.
 */
public abstract class Pack {

    private final String name;
    private final String description;
    private final String mimicryDescription;

    public Pack(String name) {
        this(name, "Provide description.", "Provide mimicry description.");
    }

    public Pack(String name, String description, String mimicryDescription) {
        this.name = name;
        this.description = description;
        this.mimicryDescription = mimicryDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMimicryDescription() {
        return mimicryDescription;
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

    public void onUltimateEnd(Player player, TamerPack pack) {
    }

}
