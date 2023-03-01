package me.hapyl.fight.game;

import org.bukkit.inventory.ItemStack;

public interface ItemDisplay<E> {

    ItemStack createItem(E e);

}
