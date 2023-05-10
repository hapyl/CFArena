package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import me.hapyl.fight.game.cosmetic.contrail.ContrailCosmetic;
import me.hapyl.fight.game.cosmetic.contrail.ParticleContrailCosmetic;
import me.hapyl.fight.game.cosmetic.gui.CosmeticGUI;
import me.hapyl.fight.game.cosmetic.gui.PurchaseConfirmGUI;
import me.hapyl.fight.game.cosmetic.gui.PurchaseResult;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.shop.ShopItem;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class Cosmetic extends ShopItem {

    @Nonnull
    private final Type type;

    public Cosmetic(String name, String description, long cost, @Nonnull Type type, Rarity rarity, Material icon) {
        super(name, description, cost);
        this.setIcon(icon);
        this.setRarity(rarity);
        this.type = type;

        if (this instanceof Listener listener) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public Cosmetic(String name, String description, Type type, Rarity rarity, Material material) {
        this(name, description, rarity.getDefaultPrice(), type, rarity, material);
    }

    public Cosmetic(String name, String description, long cost, Type type, Rarity rarity) {
        this(name, description, cost, type, rarity, Material.BARRIER);
    }

    protected Cosmetic(String name, String description, long cost, Type type) {
        this(name, description, cost, type, Rarity.UNSET, Material.BARRIER);
    }

    public ItemActionPair createItem(Player player, Cosmetics cosmetic, CosmeticGUI previous) {
        final CosmeticEntry entry = PlayerDatabase.getDatabase(player).getCosmetics();
        final CurrencyEntry currency = PlayerDatabase.getDatabase(player).getCurrency();

        final Type type = this.getType();
        final ItemBuilder builder = this.createItem(player);

        Action action = null;

        // Check if player has the cosmetic
        if (entry.hasCosmetic(cosmetic)) {
            // Check if it's selected
            if (entry.getSelected(type) == cosmetic) {
                builder.addLore();
                builder.addLore("&a&lSELECTED");
                builder.addLore("&eClick to deselect this cosmetic.");
                builder.glow();

                action = pl -> {
                    entry.unsetSelected(type);
                    Chat.sendMessage(pl, "&aYou have deselected your %s cosmetic.", Chat.capitalize(type.name()));
                    PlayerLib.playSound(pl, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f);
                    previous.updateInventory();
                };
            }
            else {
                builder.addLore();
                builder.addLore("&2&lUNLOCKED");
                builder.addLore("&eClick to select this cosmetic.");

                action = pl -> {
                    entry.setSelected(type, cosmetic);
                    Chat.sendMessage(pl, "&aYou have selected your %s cosmetic.", Chat.capitalize(type.name()));
                    PlayerLib.playSound(pl, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f);
                    previous.updateInventory();
                };
            }
        }
        else {
            builder.addLore();
            builder.addLore("&c&lLOCKED");

            // Check if it's purchasable
            if (getCost() != ShopItem.NOT_PURCHASABLE) {
                builder.addLore("&7Price: &e" + getCost() + " Coins");
                builder.addLore();

                // Check if player can afford it
                if (currency.get(Currency.COINS) >= getCost()) {
                    builder.addLore("&eClick to purchase this cosmetic.");

                    action = pl -> new PurchaseConfirmGUI(player, this) {
                        @Override
                        public void onPurchase(Player player, ShopItem item, PurchaseResult result) {
                            Chat.sendMessage(player, result.getMessage());

                            if (result == PurchaseResult.OK) {
                                entry.addOwned(cosmetic);
                                Chat.sendMessage(player, "&aYou have purchased the %s cosmetic.", Chat.capitalize(type.name()));
                                PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
                            }

                            new CosmeticGUI(player, type);
                        }
                    };

                }
                else {
                    builder.addLore("&cCannot afford!");
                }
            }
            else {
                builder.addLore("&cThis cosmetic is not purchasable.");
            }
        }

        return new ItemActionPair(builder.asIcon(), action);
    }

    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player).removeLore();

        final Rarity rarity = getRarity();

        // Rarity Type Cosmetic
        builder.addLore("&8%s %s Cosmetic", rarity.getName(), Chat.capitalize(getType()));
        builder.addLore("");
        builder.addSmartLore("&7" + getDescription());

        if (getExtra() != null) {
            builder.addLore();
            builder.addSmartLore("&7" + getExtra());
        }

        if (this instanceof ContrailCosmetic) {
            builder.addLore();
            if (this instanceof ParticleContrailCosmetic) {
                builder.addLore("&6&lThis is a particle contrail!");
                builder.addSmartLore("It will follow behind you and display a particle.", "&e");
            }
            else if (this instanceof BlockContrailCosmetic) {
                builder.addLore("&6This is a block contrail!");
                builder.addSmartLore("It will convert blocks you're walking on.", "&e");
            }
        }

        if (this instanceof PrefixCosmetic prefix) {
            builder.addLore();
            builder.addLore("&bPrefix Preview: ");
            builder.addLore(" " + prefix.getPrefix());
        }

        return builder;
    }

    @Override
    public Cosmetic setIcon(Material icon) {
        super.setIcon(icon);
        return this;
    }

    @Override
    public Cosmetic setExtra(String extra) {
        super.setExtra(extra);
        return this;
    }

    @Override
    public Cosmetic setRarity(Rarity rarity) {
        super.setRarity(rarity);
        return this;
    }

    public abstract void onDisplay(Display display);

    public void onDisplay(Player player) {
        onDisplay(new Display(player, player.getLocation()));
    }

    public void onDisplay(Location location) {
        onDisplay(new Display(null, location));
    }

    @Nonnull
    public Type getType() {
        return type;
    }

}
