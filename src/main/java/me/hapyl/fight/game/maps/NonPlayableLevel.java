package me.hapyl.fight.game.maps;

import org.bukkit.Material;

import javax.annotation.Nonnull;

public class NonPlayableLevel extends Level {

    public NonPlayableLevel(@Nonnull EnumLevel handle, String name, String description, int x, int y, int z) {
        this(handle, name, description, x, y, z, 0.0f, 0.0f);
    }

    public NonPlayableLevel(@Nonnull EnumLevel handle, String name, String description, int x, int y, int z, float yaw, float pitch) {
        super(handle, name);

        setPlayable(false);
        setMaterial(Material.BEDROCK);
        setDescription(description);
        addLocation(x, y, z, yaw, pitch);
    }

}
