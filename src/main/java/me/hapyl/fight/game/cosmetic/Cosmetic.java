package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.ItemDisplay;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.shop.ShopItem;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Cosmetic extends ShopItem implements ItemDisplay<Player> {

    private final Type type;

    public Cosmetic(String name, String description, long cost, Type type, Rarity rarity) {
        super(name, description, cost);
        this.setRarity(rarity);
        this.type = type;

        if (this instanceof Listener listener) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    @Override
    public ItemStack createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(getIcon(), getName());

        builder.addLore("&8" + Chat.capitalize(type) + " Cosmetic");
        builder.addLore();
        builder.addSmartLore("&7" + getDescription());

        return builder.asIcon();
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

    protected Cosmetic(String name, String description, long cost, Type type) {
        this(name, description, cost, type, Rarity.UNSET);
    }

    public abstract void onDisplay(Display display);

    public void onDisplay(Player player) {
        onDisplay(new Display(player, player.getLocation()));
    }

    public void onDisplay(Location location) {
        onDisplay(new Display(null, location));
    }

    public Type getType() {
        return type;
    }
}
