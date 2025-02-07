package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class CookieMadnessCosmetic extends Cosmetic {

    public CookieMadnessCosmetic(@Nonnull Key key) {
        super(key, "Cookie Madness", Type.KILL);

        setDescription("""
                More cookies! Mo-o-o-re!
                """
        );

        setRarity(Rarity.UNCOMMON);
        setIcon(Material.COOKIE);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        display.repeat(10, 2, (r, tick) -> {
            display.dropItem(Material.COOKIE, 60);
            display.sound(Sound.ENTITY_ITEM_PICKUP, 0.0f + (tick * 0.1f));
        });
    }
}
