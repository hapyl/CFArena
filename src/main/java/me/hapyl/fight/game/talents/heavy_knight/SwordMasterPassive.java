package me.hapyl.fight.game.talents.heavy_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;

import javax.annotation.Nonnull;

public class SwordMasterPassive extends PassiveTalent {
    public SwordMasterPassive(@Nonnull Key key) {
        super(key, "tbd");

        setDescription("""
                """
        );

    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
