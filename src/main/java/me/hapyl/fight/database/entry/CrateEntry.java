package me.hapyl.fight.database.entry;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.EnumMappedEntry;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.eterna.module.util.Compute;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class CrateEntry extends PlayerDatabaseEntry implements EnumMappedEntry<Crates, Long> {
    public CrateEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public long getCrates(@Nonnull Crates crate) {
        return getValue("crates." + crate.name(), 0L);
    }

    public boolean hasCrate(@Nonnull Crates crate) {
        return getCrates(crate) > 0L;
    }

    public void setCrate(@Nonnull Crates crate, long amount) {
        setValue("crates." + crate.name(), amount);
    }

    public void addCrate(@Nonnull Crates crate) {
        addCrate(crate, 1);
    }

    public void addCrate(@Nonnull Crates crate, long amount) {
        setCrate(crate, getCrates(crate) + amount);
    }

    public void removeCrate(@Nonnull Crates crate) {
        removeCrate(crate, 1);
    }

    public void removeCrate(@Nonnull Crates crate, long amount) {
        setCrate(crate, getCrates(crate) - amount);
    }

    @Nonnull
    @Override
    public Crates[] enumValues() {
        return Crates.values();
    }

    @Nonnull
    @Override
    public Long getMappedValue(@Nonnull Crates crates) {
        return getCrates(crates);
    }

    public long getTotalCratesCount() {
        long count = 0;

        for (Crates value : Crates.values()) {
            count += getCrates(value);
        }

        return count;
    }

    @Nonnull
    public Map<Crates, Long> getTotalCrates() {
        final Map<Crates, Long> values = Maps.newHashMap();

        for (Crates crate : Crates.values()) {
            final long crates = getCrates(crate);

            if (crates > 0) {
                values.compute(crate, Compute.longAdd(crates));
            }
        }

        return values;
    }

    public void addAll(@Nonnull Set<Crates> crates) {
        for (Crates crate : crates) {
            addCrate(crate, 1);
        }
    }

}
