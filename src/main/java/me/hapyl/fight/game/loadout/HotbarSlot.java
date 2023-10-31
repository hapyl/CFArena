package me.hapyl.fight.game.loadout;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Described;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class HotbarSlot implements Described {

    private final Material material;
    private final String name;
    private final String description;
    private final boolean canModify;

    public HotbarSlot(Material material, String name, String description) {
        this(material, name, description, true);
    }

    public HotbarSlot(Material material, String name, String description, boolean canModify) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.canModify = canModify;
    }

    public Material getMaterial() {
        return material;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public boolean handle(@Nonnull GamePlayer player, int slot) {
        return false;
    }

}
