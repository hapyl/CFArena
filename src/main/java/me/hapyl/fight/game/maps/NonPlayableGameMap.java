package me.hapyl.fight.game.maps;

import org.bukkit.Material;

public class NonPlayableGameMap extends GameMap {

    public NonPlayableGameMap(String name, String description, double x, double y, double z) {
        super(name);

        setPlayable(false);
        setMaterial(Material.BEDROCK);
        setDescription(description);
        addLocation(x, y, z);
    }

}
