package me.hapyl.fight.gui;

import me.hapyl.fight.util.ItemStacks;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class ConfirmGUI extends PlayerGUI {

    private static final Set<Integer> CONFIRM_SLOTS = Set.of(
            1, 2, 3,
            10, 11, 12,
            19, 20, 21
    );

    private static final Set<Integer> CANCEL_SLOTS = Set.of(
            5, 6, 7,
            14, 15, 16,
            23, 24, 25
    );

    private static final ItemStack CONFIRM_ITEM = ItemBuilder.of(Material.GREEN_WOOL, "&aConfirm", "&7I got it, proceed!").asIcon();
    private static final ItemStack CANCEL_ITEM = ItemBuilder.of(Material.RED_WOOL, "&cCancel!", "&7I've changed my mind.").asIcon();

    public ConfirmGUI(Player player, String name) {
        super(player, name, 5);

        setItem(40, quoteItem());
        fillItem(27, 35, ItemStacks.BLACK_BAR);

        setConfirmItem(CONFIRM_ITEM);
        setCancelItem(CANCEL_ITEM);

        setEventListener((player1, gui, event) -> {
            final int clicked = event.getRawSlot();

            if (CONFIRM_SLOTS.contains(clicked)) {
                onConfirm(player);
            }
            else if (CANCEL_SLOTS.contains(clicked)) {
                onCancel(player);
            }
            else {
                onMissClick(player);
            }
        });

        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 0.75f);
    }

    @Override
    public final void openInventory() {
        // force player to close inventory before opening the GUI
        // to force mouse to be at the quote item.
        player.closeInventory();
        super.openInventory();
    }

    @Nonnull
    public abstract ItemStack quoteItem();

    /**
     * Player clicked confirm.
     */
    public abstract void onConfirm(@Nonnull Player player);

    /**
     * Player clicked cancel.
     */
    public abstract void onCancel(@Nonnull Player player);

    /**
     * Player clicked any other slot than {@link #CONFIRM_SLOTS} or {@link #CANCEL_SLOTS}.
     */
    public void onMissClick(@Nonnull Player player) {
    }

    private void setConfirmItem(ItemStack stack) {
        for (Integer confirmSlot : CONFIRM_SLOTS) {
            setItem(confirmSlot, stack);
        }
    }

    private void setCancelItem(ItemStack stack) {
        for (Integer cancelSlot : CANCEL_SLOTS) {
            setItem(cancelSlot, stack);
        }
    }

}
