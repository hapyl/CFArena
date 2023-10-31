package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Material;

import javax.annotation.Nonnull;

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
    public final Response execute(@Nonnull GamePlayer player) {
        player.sendMessage("do not execute passive talents");
        return Response.OK;
    }

    @Override
    public boolean isDisplayAttributes() {
        return false;
    }
}
