package me.hapyl.fight.game.talents.juju;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ClimbPassive extends PassiveTalent {
    public ClimbPassive(@Nonnull DatabaseKey key) {
        super(key, "Climb");

        setDescription("""
                Raised by the &ajungle&7, Juju mastered the ability to &2climb&7 &nanything&7.
                
                &6Jump&7 on a wall to grab onto it and &bdesccent&7 slowly.
                &6Sneak&7 to climb upwards.
                &6Double Jump&7 to dash backwards.
                """
        );

        setItem(Material.LEATHER_BOOTS);
    }
}
