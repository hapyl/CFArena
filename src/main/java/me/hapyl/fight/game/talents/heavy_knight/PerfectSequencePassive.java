package me.hapyl.fight.game.talents.heavy_knight;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class PerfectSequencePassive extends PassiveTalent {
    public PerfectSequencePassive(@Nonnull DatabaseKey key) {
        super(key, "Perfect Sequence");

        setDescription("""
                Using %1$s ❱ %2$s ❱ %3$s in quick &2&nsuccession&7 &cempowers&7 and &nresets&7 the cooldown of %3$s.
                """.formatted(TalentRegistry.UPPERCUT, TalentRegistry.UPDRAFT, TalentRegistry.SLASH)
        );

        setItem(Material.CLOCK);
        setCooldownSec(5);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
