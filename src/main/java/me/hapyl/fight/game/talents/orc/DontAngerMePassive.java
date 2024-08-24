package me.hapyl.fight.game.talents.orc;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DontAngerMePassive extends PassiveTalent {
    public DontAngerMePassive(@Nonnull DatabaseKey key) {
        super(key, "Don't Anger Me");

        setDescription("""
                Taking &ncontinuous&7 &cdamage&7 within the set time window will trigger %s for &b3s&7.
                """.formatted(Named.BERSERK)
        );

        setItem(Material.FERMENTED_SPIDER_EYE);
    }
}