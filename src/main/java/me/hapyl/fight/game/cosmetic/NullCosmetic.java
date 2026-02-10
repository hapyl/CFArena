package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.color.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

@ApiStatus.Internal
public class NullCosmetic extends Cosmetic {
    public NullCosmetic(@Nonnull Key key) {
        super(key, "No offer!", Type.DEATH);

        setDescription("""
                It looks like you own all the items, so there is nothing to buy or generate!
                """
        );

        setExclusive(true);
    }

    @Override
    public boolean isPurchasable() {
        return false;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = ItemBuilder.of(Material.BARRIER, getName());

        builder.addLore("");
        builder.addTextBlockLore(getDescription());

        return builder;
    }

    @Nonnull
    @Override
    public String getRarityString() {
        return Color.BUTTON + "Click for details";
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
    }
}
