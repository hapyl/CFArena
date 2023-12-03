package me.hapyl.fight.game.talents.archive;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Discharge extends Talent {
    public Discharge() {
        super("Quantum Discharge");

        setType(Type.DAMAGE);
        setItem(Material.IRON_TRAPDOOR);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
