package me.hapyl.fight.game.talents.doctor;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BlockMaelstromPassive extends PassiveTalent {

    @DisplayField public final int cooldown = Tick.fromSecond(10);

    public BlockMaelstromPassive(@Nonnull Key key) {
        super(key, "Block Maelstrom");

        setDescription("""
                Creates a &bblock&7 that orbits around you, dealing &cdamage&7 upon contact with an enemy based on the element.
                &8&o;;Refreshes every %ss.
                """.formatted(Tick.round(cooldown))
        );

        setMaterial(Material.BRICK);
        setType(TalentType.DEFENSE);
    }
}
