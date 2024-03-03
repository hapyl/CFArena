package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.collectible.relic.Type;
import org.bukkit.entity.Player;

import java.util.List;

public class CollectibleEntry extends PlayerDatabaseEntry {

    public static final int PERMANENT_EXCHANGE_RATE = 5;

    /*
     * collectibles: {
     *  found: [],
     *  claimed: {
     *      TYPE:
     *  },
     *  exchange: INT
     * },
     */

    public CollectibleEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
        setPath("collectibles");
    }

    public boolean hasClaimed(Type type, int tier) {
        return getValue(getPath() + ".claimed.%s%s".formatted(type.name(), tier), false);
    }

    public boolean canClaimAnyTier() {
        for (Type type : Type.values()) {
            for (int i = 1; i <= 3; i++) {
                if (hasClaimed(type, i)) {
                    continue;
                }

                if (canClaim(type, i)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canClaim(Type type, int index) {
        final RelicHunt relicHunt = Main.getPlugin().getRelicHunt();
        final Player player = getOnlinePlayer();

        if (player == null) {
            return false;
        }

        final int totalRelics = relicHunt.byType(type).size();
        final int foundRelics = relicHunt.getFoundListByType(player, type).size();
        final boolean anyClaimable = totalRelics > 0 && foundRelics > 0;

        return switch (index) {
            case 1 -> anyClaimable;
            case 2 -> anyClaimable && foundRelics >= (totalRelics / 2);
            case 3 -> anyClaimable && foundRelics >= totalRelics;
            default -> false;
        };
    }

    public boolean canLevelUpStabilizer() {
        final Player player = getOnlinePlayer();

        if (player == null) {
            return false;
        }

        final RelicHunt relicHunt = Main.getPlugin().getRelicHunt();
        final List<Relic> foundList = relicHunt.getFoundList(player);
        final int totalExchanged = getPermanentExchangeCount();
        final int canExchange = foundList.size() - totalExchanged;

        return canExchange >= PERMANENT_EXCHANGE_RATE;
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

    /**
     * Returns true if the player has any missing relics.
     *
     * @return true if the player has any missing relics; false otherwise.
     */
    public boolean anyMissing() {
        return !hasFoundAll();
    }

    /**
     * Returns true if the player has found all the relics.
     *
     * @return true if the player has found all the relics; false otherwise.
     */
    public boolean hasFoundAll() {
        final RelicHunt relicHunt = Main.getPlugin().getRelicHunt();
        final int totalRelics = relicHunt.getTotalRelics();
        final int size = getFoundList().size();

        return size >= totalRelics;
    }
}
