package me.hapyl.fight.game.talents.archive.shaman.resonance;

import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public abstract class TotemResonance implements Described, DisplayFieldProvider {

    private final Material material;
    private final String name;
    private final String description;

    private int interval;
    private Talent.Type type;
    private DisplayData displayData;

    protected TotemResonance(Material material, String name, String description) {
        this.material = material;
        this.name = name;
        this.description = description;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setDisplayData(@Nonnull String data) {
        this.displayData = BlockStudioParser.parse(data);
    }

    public void setType(@Nonnull Talent.Type type) {
        this.type = type;
    }

    @Nonnull
    public DisplayData getDisplayData() {
        return displayData;
    }

    /**
     * Resonates with a totem at the given {@link #interval}.
     *
     * @param totem - A totem.
     */
    public abstract void resonate(@Nonnull Totem totem);

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

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    public int getInterval() {
        return interval;
    }

    @Nonnull
    public Talent.Type getType() {
        return type;
    }
}
