package me.hapyl.fight.game.talents.dark_mage;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;

import javax.annotation.Nonnull;

public class WitherRosePassive extends PassiveTalent {
    public WitherRosePassive(@Nonnull DatabaseKey key) {
        super(key, "Wither Rose");

        setDescription("""
                Dealing &4damage&7 plants a %1$s&7 into the &cenemy&7.
                
                &nEach&7 stack of %1$s increases the &nduration&7 of your ultimate.
                """.formatted(Named.WITHER_ROSE)
        );
    }
}
