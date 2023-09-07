package me.hapyl.fight.gui.styled;

import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
            gui.setItem(
                    size - 5,
                    ItemBuilder.of(Material.BARRIER, "&cClose", "Click to close the menu!").asIcon(),
                    HumanEntity::closeInventory
            );

            setReturn(gui, gui.getSize() - 8);
        }

        gui.onUpdate();
    }

    public static <T extends PlayerGUI & Styled> void setReturn(@Nonnull T gui, int slot) {
        final ReturnData returnData = gui.getReturnData();

        if (returnData == null) {
            return;
        }

        gui.setItem(
                slot,
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
        index = gui.getSize() - (9 - index);

        gui.setItem(index, item, action);
    }

    public static <T extends PlayerGUI & Styled> void fillRow(T gui, int row, ItemStack item) {
        row = Numbers.clamp((row + 1) * 9, 0, 8);
        gui.fillItem(Math.max(row - 9, 0), Math.min(row, gui.getSize() - 1), item);
    }

    public static <T extends PlayerGUI & Styled> void openFx(T gui) {
        PlayerLib.playSound(gui.getPlayer(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f);
    }
}
