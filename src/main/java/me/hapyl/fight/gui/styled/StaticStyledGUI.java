package me.hapyl.fight.gui.styled;

import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Since both {@link StyledGUI} and {@link StyledPageGUI} have to inherit a different class,
 * it's either code-paste code, or use static.
 * <p>
 * I choose static any day of the week.
 */
public final class StaticStyledGUI {

    public static <T extends PlayerGUI & Styled> void updateInventory(@Nonnull T gui) {
        final Size styleSize = gui.getStyleSize();

        if (styleSize != Size.NOT_STYLED) {
            final int size = gui.getSize();

            gui.fillItem(size - 9, size - 1, ItemStacks.BLACK_BAR);

            if (gui.isSetCloseButton()) {
                gui.setItem(
                        size - 5,
                        ItemBuilder.of(Material.BARRIER, "&cClose", "Click to close the menu!").asIcon(),
                        HumanEntity::closeInventory
                );
            }

            setReturn(gui);
        }

        gui.onUpdate();
    }

    public static <T extends PlayerGUI & Styled> void setReturn(@Nonnull T gui) {
        final ReturnData returnData = gui.getReturnData();

        if (returnData == null) {
            return;
        }

        final int slot = returnData.getSlot();

        gui.setItem(
                gui.getSize() - (9 - slot),
                StyledTexture.ARROW_LEFT.toBuilder().setName("&aGo Back").addLore("To " + returnData.getName()).asIcon(),
                player -> {
                    final Action<Player> action = returnData.getAction();

                    action.use(player);
                }
        );
    }

    public static <T extends PlayerGUI & Styled> void setHeader(@Nonnull T gui, @Nonnull ItemStack item) {
        gui.fillRow(0, ItemStacks.BLACK_BAR);
        gui.setItem(4, item);
    }

    public static <T extends PlayerGUI & Styled> void setPanelItem(T gui, int index, ItemStack item, me.hapyl.spigotutils.module.inventory.gui.Action action) {
        setPanelItem(gui, index, item, action, ClickType.LEFT);
    }

    public static <T extends PlayerGUI & Styled> void setPanelItem(T gui, int index, ItemStack item, me.hapyl.spigotutils.module.inventory.gui.Action action, ClickType... clickTypes) {
        index = gui.getSize() - (9 - index);

        gui.setItem(index, item, action, getClickTypes(clickTypes));
    }

    public static <T extends PlayerGUI & Styled> void fillRow(T gui, int row, ItemStack item) {
        final int rowSize = row * 9;

        gui.fillItem(rowSize, rowSize + 8, item);
    }

    public static <T extends PlayerGUI & Styled> void openFx(T gui) {
        PlayerLib.playSound(gui.getPlayer(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f);
    }

    @Nonnull
    private static ClickType[] getClickTypes(ClickType[] clickTypes) {
        if (clickTypes == null || clickTypes.length == 0) {
            return new ClickType[] { ClickType.LEFT };
        }

        return clickTypes;
    }
}
