package me.hapyl.fight.game.talents.storage.harbinger;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TidalWaveTalent extends Talent {

    @DisplayField protected final short height = 5;
    @DisplayField protected final double horizontalOffset = 1.0d;
    @DisplayField protected final double maxDistance = 10.0d;
    @DisplayField protected final int riptideDuration = 100;
    @DisplayField(suffix = "blocks") protected final short width = 2;

    public TidalWaveTalent() {
        super("Tidal Wave", "Summon a giant wave that pushes enemies away from you and applies Riptide to them.");

        setItem(Material.PRISMARINE_CRYSTALS);
        setCdSec(12);
        setDuration(60);
    }

    @Override
    public Response execute(Player player) {
        new TidalWave(player, getDuration());

        return Response.OK;
    }

}
