package me.hapyl.fight.game.talents.storage.harbinger;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

public class TidalWave extends Talent {
    public TidalWave() {
        super("Tidal Wave", "Summon a giant wave that pushes enemies away from you.");
    }

    @Override
    public Response execute(Player player) {


        return Response.OK;
    }
}
