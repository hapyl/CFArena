package me.hapyl.fight.game.cosmetic;

import me.hapyl.eterna.module.registry.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class HatCosmetic extends Cosmetic {

    public HatCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull Type type) {
        super(key, name, type);
    }

    @Override
    public final void setIcon(@Nonnull Material icon) {
        // TODO (Fri, Aug 30 2024 @xanyjl):
    }

    @Override
    public void onDisplay(@Nonnull Display display) {

    }
}
