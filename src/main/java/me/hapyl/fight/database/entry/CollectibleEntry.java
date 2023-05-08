package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.collectible.relic.Relic;

import java.util.List;

public class CollectibleEntry extends PlayerDatabaseEntry {

    public CollectibleEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
        setPath("collectibles");
    }

    /**
     * collectibles: {
     * found: []
     * }
     */

    public boolean hasFound(Relic relic) {
        return getFoundList().contains(relic.getId());
    }

    public void addFound(Relic relic) {
        final List<Integer> foundList = getFoundList();

        if (foundList.contains(relic.getId())) {
            return;
        }

        foundList.add(relic.getId());
        setValue(path() + ".found", foundList);
    }

    public void removeFound(Relic relic) {
        final List<Integer> foundList = getFoundList();

        foundList.remove(relic.getId());
        setValue(path() + ".found", foundList);
    }

    public List<Integer> getFoundList() {
        return getValue(path() + ".found", Lists.newArrayList());
    }

}
