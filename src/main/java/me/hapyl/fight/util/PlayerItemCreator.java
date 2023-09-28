package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface PlayerItemCreator {

    @Nonnull
    ItemBuilder create(@Nonnull Player player);

    @Nonnull
    default ItemStack createAsIcon(@Nonnull Player player) {
        return create(player).asIcon();
    }

}
