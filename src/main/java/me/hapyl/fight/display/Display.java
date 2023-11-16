package me.hapyl.fight.display;

import me.hapyl.spigotutils.module.util.BFormat;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Display {

    private final String name;

    @Nonnull private String description;
    @Nonnull private Material material;

    public Display(@Nonnull String name) {
        this.name = name;
        this.description = "";
        this.material = Material.BEDROCK;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    public Display with(@Nonnull String description, @Nullable Object... format) {
        this.description = BFormat.format(description, format);
        return this;
    }

    public Display with(@Nonnull Material material) {
        Validate.isTrue(material.isItem(), "Material must be an item!");
        this.material = material;
        return this;
    }

    public static Display of(@Nonnull String name) {
        return new Display(name);
    }

}
