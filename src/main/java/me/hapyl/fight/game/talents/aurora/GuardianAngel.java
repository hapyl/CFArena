package me.hapyl.fight.game.talents.aurora;


import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class GuardianAngel extends PassiveTalent {

    @DisplayField(suffix = "blocks") public final double maxDistance = 50d;
    @DisplayField public final double healingRadius = 3d;
    @DisplayField public final double healing = 5.0d;
    @DisplayField public final double lookupRadius = 1.3d;

    @DisplayField public final int teleportDelay = 10;

    public GuardianAngel(@Nonnull Key key) {
        super(key, "Guardian Angel");

        setDescription("""
                &6&lSNEAK&7 while &etargetting&7 a &ateammate&7 to start channeling a &bguardian&7 spell.
                
                While channeling, keep &efocusing&7 the same &ateammate&7 to &dteleport&7 to them and &a&nheal&7 nearby &ateammates&7.
                &8&o;;Aurora won't be healed upon teleport.
                """);

        setType(TalentType.MOVEMENT);
        setItem(Material.FEATHER);

        setCooldownSec(8);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
