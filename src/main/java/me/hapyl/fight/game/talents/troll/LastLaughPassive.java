package me.hapyl.fight.game.talents.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class LastLaughPassive extends PassiveTalent {

    @DisplayField(percentage = true) public final double chance = 0.01d;

    public LastLaughPassive(@Nonnull Key key) {
        super(key, "Last Laugh");

        setDescription("""
                Your hits have &b{chance}&7 chance to instantly kill the &cenemy&7.
                
                &8&oDoes not work on bosses and mini-bosses.
                """
        );

        setMaterial(Material.BLAZE_POWDER);
    }
}
