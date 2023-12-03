package me.hapyl.fight.database;

import java.util.UUID;

public class ReadOnlyPlayerDatabase extends PlayerDatabase {
    public ReadOnlyPlayerDatabase(UUID uuid) {
        super(uuid);
    }
}
