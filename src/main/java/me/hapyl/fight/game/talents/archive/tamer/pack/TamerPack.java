package me.hapyl.fight.game.talents.archive.tamer.pack;

import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.archive.tamer.TamerTimed;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * Represents a blueprint for a pack of entities.
 */
public abstract class TamerPack implements Described, TamerTimed, DisplayFieldProvider {

    protected final Attributes attributes;

    private final String name;
    private final String description;
    private final Talent.Type type;
    private int duration;

    public TamerPack(String name, String description, Talent.Type type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.duration = 100;
        this.attributes = new Attributes();
    }

    @Nonnull
    public String toString(ActiveTamerPack pack) {
        final TamerEntity<?> firstEntity = pack.getFirstEntity();

        return firstEntity != null ? firstEntity.getHealthFormatted() : "";
    }

    @Nonnull
    public Talent.Type getType() {
        return type;
    }

    @Nonnull
    public Attributes getAttributes() {
        return attributes;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Deprecated
    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public TamerPack setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public int spawnAmount() {
        return 1;
    }

    public abstract void onSpawn(@Nonnull ActiveTamerPack pack, @Nonnull Location location);

    @Nonnull
    public String getTypeString() {
        return "&8%s  &c%.0f ❤  &e⌚ %s".formatted(type.getName(), attributes.getHealth(), CFUtils.decimalFormatTick(duration));
    }
}
