package me.hapyl.fight.game.talents.juju;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.talents.PassiveTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ClimbPassive extends PassiveTalent {
    public ClimbPassive(@Nonnull Key key) {
        super(key, "Climb");

        setDescription("""
                Raised by the &ajungle&7, Juju mastered the ability to &2climb&7 anything.
                
                &8• &6Jump&7 onto a wall to grab onto it and descend slowly.
                &8• &6Jump&7 again to climb upwards.
                &8• &6Sneak&7 to do a backflip.
                """
        );

        setMaterial(Material.LEATHER_BOOTS);
        setCooldownSec(6);
    }
}
