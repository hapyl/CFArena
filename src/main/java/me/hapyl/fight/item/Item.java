package me.hapyl.fight.item;

import me.hapyl.fight.util.PatternId;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class Item extends PatternId {

    public static final Pattern ID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final Material material;
    private String name;

    public Item(@Nonnull String string, Material material) {
        super(ID_PATTERN, string);
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
