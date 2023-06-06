package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class EngineerTalent extends Talent {

    private final int ironCost;

    public EngineerTalent(@Nonnull String name, int ironCost) {
        super(name);

        this.ironCost = ironCost;
    }

    public int getIronCost() {
        return ironCost;
    }

    public abstract Response create(Player player);

    @Override
    public final Response execute(Player player) {
        final int playerIron = Heroes.ENGINEER.getHero(Engineer.class).getIron(player);

        if (playerIron < ironCost) {
            return Response.error("Not enough iron!");
        }

        return create(player);
    }
}
