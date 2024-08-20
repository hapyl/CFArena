package me.hapyl.fight.game.talents.aurora;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DivineIntervention extends PassiveTalent {

    @DisplayField(suffix = "blocks") public final double maxDistance = 50d;

    public DivineIntervention(@Nonnull DatabaseKey key) {
        super(key, "Guardian Angel");

        setDescription("""
                &6&lSNEAK&7 to instantly &b&nteleport&7 to the &etarget &ateammate&7.
                """);

        setItem(Material.FEATHER);
        setCooldownSec(8);
    }

    @Override
    public boolean isDisplayAttributes() {
        return true;
    }
}
