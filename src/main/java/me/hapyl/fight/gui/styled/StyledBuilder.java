package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.color.Color;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface StyledBuilder {

    @Nonnull
    default ItemStack asIcon(@Nonnull String name, @Nonnull String... lore) {
        final ItemBuilder builder = toBuilder().setName(name);

        for (String string : lore) {
            builder.addLore(string);
        }

        return builder.asIcon();
    }

    @Nonnull
    default ItemStack asIconWithLore(@Nonnull String... lore) {
        final ItemBuilder builder = toBuilder();

        for (String s : lore) {
            builder.addLore(s);
        }

        return builder.asIcon();
    }

    @Nonnull
    default ItemStack asIcon() {
        return toBuilder().asIcon();
    }

    @Nonnull
    default ItemStack asButton(@Nonnull String clickTo) {
        return toBuilder().addLore().addLore(Color.BUTTON.color("Click to {action}!", clickTo)).asIcon();
    }

    default ItemStack asButton(@Nonnull String name, @Nonnull String clickTo) {
        return toBuilder().setName(name).addLore().addLore(Color.BUTTON.color("Click to {action}!", clickTo)).asIcon();
    }

    @Nonnull
    ItemBuilder toBuilder();

}
