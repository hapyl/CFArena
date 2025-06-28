package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.MongoUtils;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.challenge.Challenge;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.registry.Registries;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class StoreEntry extends PlayerDatabaseEntry {

    public static final int STORE_SIZE = 4;

    public StoreEntry(@Nonnull PlayerDatabase database) {
        super(database, "store");
    }

    //
    // store
    // {
    //   offers:
    //   [
    //     "offer_1",
    //     "offer_2",
    //     "offer_3",
    //     "offer_4"
    //   ],
    //   day: 12345
    // }

    public Cosmetic[] generateOffers() {
        final Cosmetic[] offers = Cosmetic.getCosmeticsThatCanAppearInStoreAndNotOwnedBy(player().orElseThrow());

        setValue("offers", MongoUtils.getKeys(offers));
        setValue("day", Challenge.getCurrentDay());

        return offers;
    }

    public Cosmetic[] getOffers() {
        final ArrayList<String> offers = getValue("offers", Lists.newArrayList());
        final int day = getValue("day", -1);
        final int currentDay = Challenge.getCurrentDay();

        // Empty means no offers
        if (offers.isEmpty() || day != currentDay) {
            return generateOffers();
        }

        final Cosmetic[] cosmetics = new Cosmetic[STORE_SIZE];

        for (int i = 0; i < Math.min(STORE_SIZE, offers.size()); i++) {
            cosmetics[i] = Registries.cosmetics().get(offers.get(i));
        }

        return cosmetics;
    }

    public long getCoinsSpent() {
        return getValue("coins_spent", 0L);
    }

    public void addCoinsSpent(long amount) {
        setValue("coins_spent", getCoinsSpent() + amount);
    }
}
