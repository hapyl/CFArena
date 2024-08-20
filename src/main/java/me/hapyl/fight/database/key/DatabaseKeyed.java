package me.hapyl.fight.database.key;

import javax.annotation.Nonnull;

public interface DatabaseKeyed {

    @Nonnull
    DatabaseKey getDatabaseKey();

}
