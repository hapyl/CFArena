package me.hapyl.fight.game.cosmetic.win;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class HellboundWinCosmetic extends WinCosmetic {
    public HellboundWinCosmetic(@Nonnull Key key) {
        super(key, "Hellbound");

        setDescription("""
                It was bound to happen...
                """
        );

        setRarity(Rarity.CURSED);
        setIcon(Material.NETHER_BRICK);
    }

    @Override
    public void onStart(@Nonnull Display display) {
    }

    @Override
    public void onStop(@Nonnull Display display) {

    }

    @Override
    public void tickTask(@Nonnull Display display, int tick) {

    }
}
