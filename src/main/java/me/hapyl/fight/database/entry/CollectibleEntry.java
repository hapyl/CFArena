package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.Type;

import java.util.List;

public class CollectibleEntry extends PlayerDatabaseEntry {

    public CollectibleEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
        setPath("collectibles");
    }

    /**
     * collectibles: {
     * found: [],
     * claimed: {
     * TYPE:
     * },
     * exchange: INT
     * },
     */

    public boolean hasClaimed(Type type, int tier) {
        return getValue(getPath() + ".claimed.%s%s".formatted(type.name(), tier), false);
    }

    public void setClaimed(Type type, int tier, boolean flag) {
        setValue(getPath() + ".claimed.%s%s".formatted(type.name(), tier), flag);
    }

    public boolean hasFound(Relic relic) {
        return getFoundList().contains(relic.getId());
    }

    public void addFound(Relic relic) {
        final List<Integer> foundList = getFoundList();

        if (foundList.contains(relic.getId())) {
            return;
        }

        foundList.add(relic.getId());
        setValue(getPath() + ".found", foundList);
    }

    public void removeFound(Relic relic) {
        final List<Integer> foundList = getFoundList();

        foundList.remove(relic.getId());
        setValue(getPath() + ".found", foundList);
    }

    public List<Integer> getFoundList() {
        return getValue(getPath() + ".found", Lists.newArrayList());
    }

    public int getPermanentExchangeCount() {
        return getValue(getPath() + ".exchange", 0);
    }

    public void incrementPermanentExchangeCount(int value) {
        setValue(getPath() + ".exchange", getPermanentExchangeCount() + value);
    }
}
