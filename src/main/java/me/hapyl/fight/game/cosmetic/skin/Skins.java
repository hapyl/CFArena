package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.heroes.Heroes;

// Skins are separated from the rest of the cosmetics because they are handled differently.
public enum Skins {

    // Namespace is HERO:SKINNAME

    ARCHER_HUNTER(new Skin(Heroes.ARCHER, "Hunter")),

    ;

    private final Skin skin;

    Skins(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }
}
