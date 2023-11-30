package me.hapyl.fight.database;

import javax.annotation.Nonnull;
import java.util.Map;

public interface MappedEntry<K, V> {

    /**
     * Returns a new map of all the available values.
     *
     * @return a new map of all the available values.
     */
    @Nonnull
    Map<K, V> mapped();

}
