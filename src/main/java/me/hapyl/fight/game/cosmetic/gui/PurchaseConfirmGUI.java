package me.hapyl.fight.game.cosmetic.gui;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.shop.ShopItem;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PurchaseConfirmGUI extends PlayerGUI {

    private final ShopItem item;

    public PurchaseConfirmGUI(Player player, ShopItem item) {
        super(player, "Confirm Purchase", 3);

        this.item = item;
        updateInventory();
    }

    public abstract void onPurchase(Player player, ShopItem item, PurchaseResult result);

    private void updateInventory() {
        try {
            final CurrencyEntry currency = PlayerDatabase.getDatabase(getPlayer()).getCurrency();

            setItem(13, item.createItem(getPlayer()).asIcon());

            final ItemStack confirmPurchaseItem = ItemBuilder.of(
                    Material.LIME_CONCRETE,
                    "&aPurchase!",
                    "&7Click here to buy this item for &a%s&7.".formatted(item.getCost())
            ).asIcon();

            final ItemStack cancelPurchaseItem = ItemBuilder.of(
                    Material.RED_CONCRETE,
                    "&cCancel",
                    "&7Click here to cancel this purchase."
            ).asIcon();

            for (int i = 0; i < 3; i++) {
                fillItem((i * 9), 2 + (i * 9), confirmPurchaseItem, player -> {
                    // Double check if player has enough coins
                    if (currency.get(Currency.COINS) >= item.getCost()) {
                        currency.subtract(Currency.COINS, item.getCost());
                        this.onPurchase(player, item, PurchaseResult.OK);
                    }
                    else {
                        this.onPurchase(player, item, PurchaseResult.NOT_ENOUGH_COINS);
                    }
                });
                fillItem(6 + (i * 9), 8 + (i * 9), cancelPurchaseItem, player -> {
                    this.onPurchase(player, item, PurchaseResult.CANCELLED);
                });
            }

            openInventory();
        } catch (Exception e) {
            this.onPurchase(getPlayer(), item, PurchaseResult.ERROR);
        }
    }
}
