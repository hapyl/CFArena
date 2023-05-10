package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.cosmetic.skin.storage.SkinMegalodon;
import me.hapyl.fight.game.heroes.Heroes;

// Skins are separated from the rest of the cosmetics because they are handled differently.
public enum Skins {

    // Namespace is HERO_SKIN

    ARCHER_HUNTER(new SkinNoEffect(Heroes.ARCHER, "Hunter")),

    SHARK_MEGALODON(new SkinMegalodon()),

    ;

    private final Skin skin;

    Skins(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }
}
