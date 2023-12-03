package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EngineerRecall extends Talent {
    public EngineerRecall() {
        super("Recall");

        setDescription("""
                Destroy the current construct and regain 25%% of its original cost.
                """);

        setItem(Material.IRON_PICKAXE);
        setCooldownSec(30);
    }

    @Override
    public Response execute(Player player) {
        final Construct construct = Heroes.ENGINEER.getHero(Engineer.class).getConstruct(player);

        if (construct == null) {
            return Response.error("No construct present!");
        }

        return Response.OK;
    }
}
