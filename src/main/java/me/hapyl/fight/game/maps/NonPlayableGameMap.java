package me.hapyl.fight.game.maps;

import org.bukkit.Material;

public class NonPlayableGameMap extends GameMap {

    public NonPlayableGameMap(String name, String description, int x, int y, int z) {
        this(name, description, x, y, z, 0.0f, 0.0f);
    }

    public NonPlayableGameMap(String name, String description, int x, int y, int z, float yaw, float pitch) {
        super(name);

        setPlayable(false);
        setMaterial(Material.BEDROCK);
        setDescription(description);
        addLocation(x, y, z, yaw, pitch);
    }

}
