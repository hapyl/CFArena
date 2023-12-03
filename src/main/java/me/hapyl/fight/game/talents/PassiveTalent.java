package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PassiveTalent extends Talent {

    public PassiveTalent(String name, Material material) {
        this(name, "", material);
    }

    public PassiveTalent(String name, String description, Material item) {
        super(name, description, Type.PASSIVE);
        setItem(item);
        setPoint(0);
    }

    @Override
    public final Response execute(Player player) {
        player.sendMessage("do not execute passive talents");
        return Response.OK;
    }

    @Override
    public boolean isDisplayAttributes() {
        return false;
    }
}
