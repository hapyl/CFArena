package me.hapyl.fight.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ItemStackRandomizedData extends ItemStack {

    public ItemStackRandomizedData(Material type) {
        super(type, 1);

        final ItemMeta itemMeta = getItemMeta();

        if (itemMeta != null) {
            itemMeta.setDisplayName(UUID.randomUUID().toString());
        }

        setItemMeta(itemMeta);
    }

}
