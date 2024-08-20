package me.hapyl.fight.game.talents.doctor;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BlockMaelstromPassive extends PassiveTalent {
    public BlockMaelstromPassive(@Nonnull DatabaseKey key) {
        super(key, "Block Maelstrom");

        setDescription("""
                Creates a block that orbits around you, dealing damage based on the element upon contact with opponents.
                &7Refreshes every &b10s&7.
                """
        );

        setItem(Material.BRICK);
        setType(TalentType.DEFENSE);
    }
}
