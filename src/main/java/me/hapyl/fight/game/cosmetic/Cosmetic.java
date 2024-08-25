package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.util.Formatted;
import me.hapyl.fight.util.handle.EnumHandle;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    public Cosmetic(String name, Type type, Rarity rarity) {
        this(name, null, type, rarity, Material.BARRIER);
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

    @Override
    public Cosmetic setExclusive(boolean exclusive) {
        super.setExclusive(exclusive);
        return this;
    }

    @Override
    public Cosmetic setTexture(String texture) {
        super.setTexture(texture);
        return this;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player).removeLore();

        final Rarity rarity = getRarity();

        // Rarity Type Cosmetic
        builder.addLore(rarity.toString(type.getName()));
        builder.addLore("");
        builder.addTextBlockLore(getDescription());

        addExtraLore(builder, player);
        return builder;
    }

    @Override
    public Cosmetic setIcon(Material icon) {
        super.setIcon(icon);
        return this;
    }

    @Override
    public Cosmetic setRarity(Rarity rarity) {
        super.setRarity(rarity);
        return this;
    }

    public final void onDisplay0(Display display) {
        final Player player = display.getPlayer();

        if (this instanceof Disabled) {
            if (player != null) {
                Notifier.ERROR.send(player, "This cosmetic is currently disabled, sorry!");
            }
            return;
        }

        onDisplay(display);
    }

    public void onEquip(Player player) {
    }

    public void onUnequip(Player player) {
    }

    @Nonnull
    @Override
    public String getFormatted() {
        return ChatColor.GREEN + getName() + " &7(" + type.getName() + ") " + getRarity().getFormatted() + "&7";
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    protected abstract void onDisplay(Display display);

}
