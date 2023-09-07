package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A collection of commonly used static ItemStacks.
 */
public final class ItemStacks {

    public static final ItemStack AIR = new ItemStack(Material.AIR);
    public static final ItemStack NULL = AIR;

    public static final ItemStack ARROW_NEXT_PAGE = ItemBuilder.playerHead(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
            )
            .setName("&aNext Page")
            .toItemStack();

    public static final ItemStack ARROW_PREV_PAGE
            = ItemBuilder.playerHead(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
            )
            .setName("&aPrevious Page")
            .toItemStack();

    public static final ItemStack BLACK_BAR
            = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").toItemStack();

    public static final ItemStack OAK_QUESTION = ItemBuilder.playerHeadUrl("badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a")
            .setName("&a???")
            .asIcon();

    public static final ItemStack OAK_EXCLAMATION = ItemBuilder.playerHeadUrl(
            "2e3f50ba62cbda3ecf5479b62fedebd61d76589771cc19286bf2745cd71e47c6").setName("&a!!!").asIcon();

    private ItemStacks() {
    }

}
