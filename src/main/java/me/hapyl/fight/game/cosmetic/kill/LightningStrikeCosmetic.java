package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LightningStrikeCosmetic extends Cosmetic {
    public LightningStrikeCosmetic(@Nonnull Key key) {
        super(key, "Lightning Strike", Type.KILL);

        setDescription("""
                Strikes a lightning effect.
                """
        );

        setRarity(Rarity.COMMON);
        setIcon(Material.LIGHTNING_ROD);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        display.getWorld().strikeLightningEffect(display.getLocation());
    }
}
