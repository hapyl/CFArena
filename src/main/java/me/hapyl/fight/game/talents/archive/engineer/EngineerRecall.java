package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EngineerRecall extends Talent {
    private final int cdIfNoConst = 5 * 20;

    @DisplayField(percentage = true) private final double regainPercent = 0.5d;

    public EngineerRecall() {
        super("Recall");

        setDescription("""
                Destroy the &ncurrent&7 Construct and regain {regainPercent} of its total cost.
                """);

        setType(Type.ENHANCE);
        setItem(Material.IRON_PICKAXE);

        setCooldownSec(15);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        Engineer hero = Heroes.ENGINEER.getHero(Engineer.class);
        final Construct construct = hero.getConstruct(player);

        if (construct == null) {
            startCd(player, cdIfNoConst);
            player.sendMessage("&cNo constructions to recall!");
            return Response.AWAIT;
        }

        hero.removeConstruct(player);

        final int cost = construct.getCost();
        final int upgradeCost = construct.getUpgradeCost();
        final int totalCost = (int) ((cost + (upgradeCost * construct.getLevel())) * regainPercent);

        hero.addIron(player, totalCost);
        return Response.OK;
    }
}
