package me.hapyl.fight.game.cosmetic.win;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

        setExclusive(true);
    }

    @Override
    public void onStart(@Nonnull Display display) {
    }

    @Override
    public void onStop(@Nonnull Display display) {

    }

    @Override
    public void onTick(@Nonnull Display display, int tick) {
        final Player player = display.getPlayer();

        // TODO: I really have no idea for this effect
    }
}
