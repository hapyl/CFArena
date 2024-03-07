package me.hapyl.fight.game.cosmetic.skin;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.cosmetic.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.cosmetic.skin.archer.SkinGreenArcher;
import me.hapyl.fight.game.cosmetic.skin.archer.SkinRedHood;
import me.hapyl.fight.game.cosmetic.skin.bk.SkinRoyalKnight;
import me.hapyl.fight.game.cosmetic.skin.shark.SkinMegalodon;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.archer.Archer;
import me.hapyl.fight.game.heroes.archive.knight.BlastKnight;
import me.hapyl.fight.game.heroes.archive.shark.Shark;

import javax.annotation.Nonnull;
import java.util.List;

// Skins are separated from the rest of the cosmetics because they are handled differently.
public enum Skins {

    /**
     * {@link Archer}
     */
    GREEN_ARCHER(new SkinGreenArcher()),
    RED_HOOD(new SkinRedHood()),

    /**
     * {@link BlastKnight}
     */
    ROYAL_KNIGHT(new SkinRoyalKnight(Heroes.BLAST_KNIGHT)),

    /**
     * {@link Shark}
     */
    MEGALODON(new SkinMegalodon()),

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

        for (Skins enumSkin : values()) {
            final Skin skin = enumSkin.skin;

            if (skin.getHero() == hero && !(skin instanceof DisabledHero)) {
                list.add(enumSkin);
            }
        }

        return list;
    }
}
