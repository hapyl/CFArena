package me.hapyl.fight.game.talents.harbinger;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class RiptidePassive extends PassiveTalent {
    public RiptidePassive(@Nonnull DatabaseKey key) {
        super(key, "Riptide");

        setDescription("""
                &nFully&7 &ncharged&7 shots in %1$s or &bcritical&7 hits in %2$s apply the %3$s effect to enemies.
                
                Hitting opponents affected by %3$s in the aforementioned ways will trigger &bRiptide Slash&7, which rapidly deals damage.
                """.formatted(Named.STANCE_RANGE, Named.STANCE_MELEE, Named.RIPTIDE)
        );

        setItem(Material.HEART_OF_THE_SEA);
    }
}
