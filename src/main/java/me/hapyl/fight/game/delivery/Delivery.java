package me.hapyl.fight.game.delivery;

import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Delivery {

    private final String name;
    private final String message;
    private Material material;

    public Delivery(String name, String message) {
        this.name = name;
        this.message = message;
        this.material = Material.CHEST;
    }

    public Delivery setMaterial(Material material) {
        Validate.isTrue(material.isItem(), "Material must be an item, %s isn't!".formatted(material));
        this.material = material;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Material getMaterial() {
        return material;
    }

    public abstract void deliver(@Nonnull Player player);
}
