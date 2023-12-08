package me.hapyl.fight.game.talents.archive.juju;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.juju.ArrowType;
import me.hapyl.fight.game.heroes.archive.juju.JuJu;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class TricksOfTheJungle extends Talent implements Listener {

    @DisplayField public final int cdBetweenShots = 5;
    @DisplayField public final double ySpread = -1.5d;
    @DisplayField public final double horizontalSpread = 1.55d;
    @DisplayField public final double damage = 5.0d;

    public TricksOfTheJungle() {
        super("Tricks of the Jungle");

        setDurationSec(8);
        setCooldownSec(20);

        setType(Type.ENHANCE);
        setItem(Material.OAK_SAPLING);

        // Have to keep the description last
        setDescription(ArrowType.ELUSIVE.getTalentDescription(this));
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        Heroes.JUJU.getHero(JuJu.class).setArrowType(player, ArrowType.ELUSIVE, getDuration());

        return Response.OK;
    }

}
