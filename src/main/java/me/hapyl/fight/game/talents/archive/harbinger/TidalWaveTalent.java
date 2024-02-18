package me.hapyl.fight.game.talents.archive.harbinger;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class TidalWaveTalent extends Talent {

    @DisplayField protected final double speed = 0.75d;
    @DisplayField protected final double verticalSpread = 3.0d;
    @DisplayField protected final double horizontalSpread = 5.0d;
    @DisplayField protected final double innerToOuterSpread = 1.5d;
    @DisplayField protected final double distance = 3.0d;
    @DisplayField protected final int riptideDuration = Tick.fromSecond(12);

    public TidalWaveTalent() {
        super("Tidal Vortex", """
                Launch a &bgiant vortex&7 in front.
                                
                The vortex constantly &brushes&7 forward, applying %s and &bpushing&7 enemies along with it.
                """.formatted(Named.RIPTIDE));

        setType(Type.IMPAIR);
        setItem(Material.PRISMARINE_CRYSTALS);
        setDurationSec(3);
        setCooldownSec(12);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        new TidalWave(this, player);
        return Response.OK;
    }

}
