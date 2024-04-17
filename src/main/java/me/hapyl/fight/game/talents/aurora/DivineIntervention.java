package me.hapyl.fight.game.talents.aurora;

import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

public class DivineIntervention extends PassiveTalent {

    @DisplayField(suffix = "blocks") public final double maxDistance = 50d;

    public DivineIntervention() {
        super("Guardian Angel", Material.FEATHER);

        setDescription("""
                &6&lSNEAK&7 to instantly &b&nteleport&7 to the &etarget &ateammate&7.
                """);

        setCooldownSec(8);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
