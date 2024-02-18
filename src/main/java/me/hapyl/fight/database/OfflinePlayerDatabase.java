package me.hapyl.fight.database;

import java.util.UUID;

public final class OfflinePlayerDatabase extends PlayerDatabase {
    public OfflinePlayerDatabase(UUID uuid) {
        super(uuid);
    }

    /**
     * @throws IllegalArgumentException always.
     */
    @Override
    @Deprecated
    public void save() throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot modify OfflinePlayerDatabase");
    }
}
