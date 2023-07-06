package me.hapyl.fight.game.cosmetic.crate;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.ux.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class CrateLoot {

    private final Player player;
    private final PlayerDatabase database;
    private final Crates enumCrate;
    private final CrateChest chest;
    private Cosmetics loot;
    private boolean isNew;

    public CrateLoot(Player player, Crates enumCrate, CrateChest chest) {
        this.player = player;
        this.database = PlayerDatabase.getDatabase(player);
        this.enumCrate = enumCrate;
        this.chest = chest;

        chest.onOpen(this);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDatabase getDatabase() {
        return database;
    }

    public Crates getEnumCrate() {
        return enumCrate;
    }

    public CrateChest getChest() {
        return chest;
    }

    @Nonnull
    public Cosmetics getLoot() {
        if (loot == null) {
            createLoot();
        }

        return loot;
    }

    public boolean isLootNew() {
        return isNew;
    }

    public void createLoot() {
        if (loot != null) {
            Message.error(player, "Loot already generated!");
            return;
        }

        final Crate crate = enumCrate.getCrate();
        final ItemContents<Cosmetics> contents = crate.getContents();
        final Rarity randomRarity = (Rarity) crate.getSchema().random();

        if (randomRarity == null) {
            Message.Error.CANNOT_FETCH_CRATE_ITEM.send(player, "randomRarity");
            return;
        }

        final Cosmetics randomItem = contents.randomItem(randomRarity);

        if (randomItem == null) {
            Message.Error.CANNOT_FETCH_CRATE_ITEM.send(player, "randomItem");
            return;
        }

        // Check if player still has a crate
        if (!database.crateEntry.hasCrate(enumCrate)) {
            Message.Error.CANNOT_FIND_CRATE.send(player, enumCrate.getCrate().getName());
            return;
        }

        this.loot = randomItem;
        this.isNew = !randomItem.isUnlocked(player);

        database.crateEntry.removeCrate(enumCrate);

        if (isNew) {
            database.cosmeticEntry.addOwned(randomItem);
        }
        else {
            database.currencyEntry.add(Currency.COINS, randomItem.getRarity().getCoinCompensation());
            database.currencyEntry.add(Currency.CHEST_DUST, randomItem.getRarity().getDustCompensation());
        }
    }
}
