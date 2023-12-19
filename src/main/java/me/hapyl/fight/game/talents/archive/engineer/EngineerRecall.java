package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EngineerRecall extends Talent {
    private final int cdIfNoConst = 5*20;
    public EngineerRecall() {
        super("Recall");

        setDescription("""
                Destroy the current construct and regain 25%% of its original cost.
                """);
        setType(Type.ENHANCE);
        setItem(Material.IRON_PICKAXE);
        setCooldownSec(30);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        Engineer hero = Heroes.ENGINEER.getHero(Engineer.class);
        final Construct construct = hero.getConstruct(player);

        if (construct == null) {
            startCd(player,cdIfNoConst);
            player.sendMessage("&cNo constructions to recall!");
            return Response.AWAIT;
        }


        hero.removeConstruct(player);
        int cost = construct.getCost();

        hero.addIron(player, (int) (cost * 0.25));
        return Response.OK;
    }
}
