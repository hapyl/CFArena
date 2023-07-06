package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import me.hapyl.fight.game.cosmetic.contrail.ContrailCosmetic;
import me.hapyl.fight.game.cosmetic.contrail.ParticleContrailCosmetic;
import me.hapyl.fight.game.cosmetic.gui.CosmeticGUI;
import me.hapyl.fight.util.Formatted;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class Cosmetic extends CollectionItem implements Formatted, EnumHandle<Cosmetics> {

    @Nonnull
    private final Type type;
    private Cosmetics handle;

    public Cosmetic(String name, String description, @Nonnull Type type, Rarity rarity, Material icon) {
        super(name, description);
        this.setIcon(icon);
        this.setRarity(rarity);
        this.type = type;

        if (this instanceof Listener listener) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public Cosmetic(String name, String description, Type type, Rarity rarity) {
        this(name, description, type, rarity, Material.BARRIER);
    }

    protected Cosmetic(String name, String description, Type type) {
        this(name, description, type, Rarity.UNSET, Material.BARRIER);
    }

    @Nonnull
    @Override
    public Cosmetics getHandle() {
        return handle;
    }

    @Override
    public void setHandle(@Nonnull Cosmetics cosmetics) {
        handle = cosmetics;
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
        }

        return new ItemActionPair(builder.asIcon(), action);
    }

    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player).removeLore();

        final Rarity rarity = getRarity();

        // Rarity Type Cosmetic
        builder.addLore(rarity.toString());
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
    @Override
    public String getFormatted() {
        return ChatColor.GREEN + getName() + " &7(" + Chat.capitalize(type) + " Cosmetic) " + getRarity().getFormatted() + "&7";
    }

    @Nonnull
    public Type getType() {
        return type;
    }

}
