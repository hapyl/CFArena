package me.hapyl.fight.gui.styled;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.color.Color;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface StyledBuilder {
    
    @Nonnull
    ItemBuilder asBuilder();
    
    @Nonnull
    default ItemStack asIcon() {
        return asBuilder().asIcon();
    }
    
    @Nonnull
    default ItemStack asIcon(@Nonnull String name) {
        return asBuilder().setName(name).asIcon();
    }
    
    @Nonnull
    default ItemStack asIcon(@Nonnull String name, @Nonnull String lore) {
        return asBuilder()
                .setName(name)
                .addTextBlockLore(lore)
                .asIcon();
    }
    
    @Nonnull
    default ItemStack asButton(@Nonnull String clickTo) { // Keep parameter name clickTo so intelliJ shows the hint as `clickTo: "action"`
        return asBuilder().addLore().addLore(Color.BUTTON + "Click to %s!".formatted(clickTo)).asIcon();
    }
    
    default ItemStack asButton(@Nonnull String name, @Nonnull String clickTo) { // Keep parameter name clickTo so intelliJ shows the hint as `clickTo: "action"`
        return asBuilder().setName(name).addLore().addLore(Color.BUTTON + "Click to %s!".formatted(clickTo)).asIcon();
    }
    
}
