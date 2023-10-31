package me.hapyl.fight.item;

import me.hapyl.fight.registry.EnumId;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Item extends EnumId {

    private final Material material;
    private String name;

    public Item(@Nonnull String string, Material material) {
        super(string);
        this.material = material;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }
}
