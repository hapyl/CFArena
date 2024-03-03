package me.hapyl.fight.game.cosmetic.skin;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.cosmetic.RubyPurchasable;
import me.hapyl.fight.game.cosmetic.skin.storage.SkinMegalodon;
import me.hapyl.fight.game.heroes.Heroes;

import javax.annotation.Nonnull;
import java.util.List;

// Skins are separated from the rest of the cosmetics because they are handled differently.
public enum Skins {

    /*
     ************************************************
     ** Please follow the namespace for this enum! **
     ** Which is: "HeroName_SkinName",             **
     ** Example: "ARCHER_HUNTER"                   **
     ** - Where "ARCHER" is the name of the hero.  **
     ** - Where "HUNTER" is the name of the skin.  **
     ************************************************
     */

    ARCHER_HUNTER(new SkinNoEffect(Heroes.ARCHER, "Hunter")),

    SHARK_MEGALODON(new SkinMegalodon()),

    ;

    private final Skin skin;

    Skins(Skin skin) {
        this.skin = skin;
    }

    @Nonnull
    public Skin getSkin() {
        return skin;
    }

    @Nonnull
    public static List<Skins> byHero(@Nonnull Heroes hero) {
        final List<Skins> list = Lists.newArrayList();

        for (Skins skin : values()) {
            if (skin.skin.getHero() == hero) {
                list.add(skin);
            }
        }

        return list;
    }
}
