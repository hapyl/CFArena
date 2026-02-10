package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.commission.Commission;

import javax.annotation.Nonnull;

public class CommissionEntry extends PlayerDatabaseEntry {
    public CommissionEntry(@Nonnull PlayerDatabase database) {
        super(database, "commission");
    }

    public long exp() {
        return getValue("exp", 0L);
    }

    public void exp(long value) {
        setValue("exp", value);
    }

    public void incrementExp(long amount) {
        exp(exp() + amount);
    }

    public void decrementExp(long value) {
        incrementExp(-value);
    }

    public int level() {
        return Commission.levelByExp(exp());
    }
}
