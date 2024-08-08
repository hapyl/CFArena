package me.hapyl.fight.util;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A collection of commonly used static ItemStacks.
 */
public final class ItemStacks {

    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final ItemStack NULL = AIR;

    public static final ItemStack BLACK_BAR
            = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").toItemStack();

    public static final ItemStack OAK_QUESTION
            = ItemBuilder.playerHeadUrl("badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a")
            .setName("&a???")
            .asIcon();

    public static final ItemStack OAK_EXCLAMATION = ItemBuilder.playerHeadUrl(
            "2e3f50ba62cbda3ecf5479b62fedebd61d76589771cc19286bf2745cd71e47c6").setName("&a!!!").asIcon();

    private ItemStacks() {
    }

}
