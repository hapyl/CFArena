package me.hapyl.fight.game.term;

import me.hapyl.fight.util.Described;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Term implements Described {

    private final Material icon;
    private final Category category;
    private final String name;
    private final String description;

    public Term(Material icon, Category category, String name, String description) {
        this.icon = icon;
        this.category = category;
        this.name = name;
        this.description = description;
    }

    public Material getIcon() {
        return icon;
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
}
