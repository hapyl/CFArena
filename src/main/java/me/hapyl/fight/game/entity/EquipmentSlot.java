package me.hapyl.fight.game.entity;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EquipmentSlot {
    // bukkit
    HAND(org.bukkit.inventory.EquipmentSlot.HAND),
    OFF_HAND(org.bukkit.inventory.EquipmentSlot.OFF_HAND),
    FEET(org.bukkit.inventory.EquipmentSlot.FEET),
    LEGS(org.bukkit.inventory.EquipmentSlot.LEGS),
    CHEST(org.bukkit.inventory.EquipmentSlot.CHEST),
    HEAD(org.bukkit.inventory.EquipmentSlot.HEAD),

    // custom
    ARROW {
        @Override
        public void setItem(@Nonnull PlayerInventory inventory, @Nullable ItemStack item) {
            inventory.setItem(9, item);
        }
    };

    @Nullable
    public final org.bukkit.inventory.EquipmentSlot bukkitSlot;

    EquipmentSlot(@Nullable org.bukkit.inventory.EquipmentSlot bukkitSlot) {
        this.bukkitSlot = bukkitSlot;
    }

    EquipmentSlot() {
        this(null);
    }

    public void setItem(@Nonnull PlayerInventory inventory, @Nullable ItemStack item) {
        if (bukkitSlot != null) {
            inventory.setItem(bukkitSlot, item);
        }
    }

}
