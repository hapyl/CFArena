package me.hapyl.fight.game.talents.witcher;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ComboPassive extends PassiveTalent {
    public ComboPassive(@Nonnull DatabaseKey key) {
        super(key, "Combo");

        setDescription("""
                Dealing &bcontinuous damage&7 to the &bsame target&7 will increase your combo.
                
                Greater combo hits deal &cincreased damage&7.
                """
        );

        setItem(Material.SKELETON_SKULL);
    }
}
