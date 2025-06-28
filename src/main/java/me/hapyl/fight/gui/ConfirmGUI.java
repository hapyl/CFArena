package me.hapyl.fight.gui;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.GUIEventListener;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.Runnables;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public abstract class ConfirmGUI extends PlayerGUI implements GUIEventListener {
    
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
    
    private static final ItemStack CONFIRM_ITEM = new ItemBuilder(Material.LIME_CONCRETE)
            .setName("&a&lᴄᴏɴꜰɪʀᴍ")
            .addLore("&7&oI get it, proceed!")
            .asIcon();
    
    private static final ItemStack CANCEL_ITEM = new ItemBuilder(Material.RED_CONCRETE)
            .setName("&c&lᴄᴀɴᴄᴇʟ")
            .addLore("&7&oI've changed my mind...")
            .asIcon();
    
    private final Boolean closeConfirms;
    private boolean input;
    
    public ConfirmGUI(@Nonnull Player player, @Nonnull String name) {
        this(player, name, true);
    }
    
    public ConfirmGUI(@Nonnull Player player, @Nonnull String name, @Nullable Boolean closeConfirms) {
        super(player, name, 5);
        
        this.closeConfirms = closeConfirms;
        
        openInventory();
    }
    
    @Override
    public void onClick(int slot, @Nonnull InventoryClickEvent event) {
        if (CONFIRM_SLOTS.contains(slot)) {
            doClick(true);
        }
        else if (CANCEL_SLOTS.contains(slot)) {
            doClick(false);
        }
    }
    
    @Override
    public void onUpdate() {
        // Prepare confirm/cancel buttons
        setConfirmItem(CONFIRM_ITEM);
        setCancelItem(CANCEL_ITEM);
        
        setItem(40, quoteItem());
        
        // Fill row
        fillRow(3, ItemStacks.BLACK_BAR);
        
        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 0.75f);
    }
    
    @Override
    public void onClose(@Nonnull InventoryCloseEvent event) {
        if (closeConfirms == null) {
            return;
        }
        
        doClick(closeConfirms);
    }
    
    @Override
    public final void openInventory() {
        // Force player to close their inventory so their
        // cursor is at the quote item
        player.closeInventory();
        
        // Delay the GUI opening by 1 tick... because...
        Runnables.runLater(super::openInventory, 1);
    }
    
    @Nonnull
    public abstract ItemStack quoteItem();
    
    /**
     * The player clicked 'confirm' or closed the Confirmation GUI.
     */
    @EventLike
    public abstract void confirm(@Nonnull Player player);
    
    /**
     * The player clicked 'cancel'
     */
    @EventLike
    public abstract void cancel(@Nonnull Player player);
    
    private void doClick(boolean confirm) {
        if (!input) {
            input = true;
            
            // Close before calling events
            player.closeInventory();
            
            if (confirm) {
                confirm(player);
            }
            else {
                cancel(player);
            }
        }
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
