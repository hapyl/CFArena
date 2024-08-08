package me.hapyl.fight.game.lobby;

import me.hapyl.fight.game.color.Color;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class LobbyItem {

    private final Material material;
    private final int slot;
    private final String name;
    private final String description;

    private final ItemStack itemStack;

    public LobbyItem(Material material, int slot, String name, String description) {
        this.material = material;
        this.slot = slot;
        this.name = name;
        this.description = description;
        this.itemStack = createBuilder();
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    public int getSlot() {
        return slot;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public abstract void onClick(Player player);

    public void modifyItem(Player player, ItemBuilder builder) {
    }

    public void give(Player player) {
        final ItemBuilder builder = new ItemBuilder(itemStack);

        modifyItem(player, builder);
        player.getInventory().setItem(slot, builder.toItemStack());
    }

    private ItemStack createBuilder() {
        final ItemBuilder builder = new ItemBuilder(material, getId())
                .setName(Color.SUCCESS + name)
                .addSmartLore(description)
                .addClickEvent(LobbyItem.this::onClick);

        return builder.asIcon();
    }

    private String getId() {
        return "lobby_" + (name.replace(" ", "_").toLowerCase());
    }

}
