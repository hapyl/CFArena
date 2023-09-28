package me.hapyl.fight.item;

import me.hapyl.fight.game.registry.Registry;

import javax.annotation.Nonnull;

public final class ItemRegistry extends Registry<Item> {
    private ItemRegistry(@Nonnull Class<Item> clazz) {
        super(clazz);
    }
}
