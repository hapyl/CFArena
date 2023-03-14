package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PassiveTalent extends Talent {

    public PassiveTalent(String name, String description, Material item) {
        super(name, description, Type.PASSIVE);
        this.setItem(item);
        this.setPoint(0);
    }

    @Override
    public final Response execute(Player player) {
        return Response.OK;
    }
}
