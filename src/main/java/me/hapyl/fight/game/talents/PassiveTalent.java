package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class PassiveTalent extends Talent {

    public PassiveTalent(@Nonnull String name, @Nonnull Material material) {
        this(name, "", material);
    }

    public PassiveTalent(@Nonnull String name, @Nonnull String description, @Nonnull Material material) {
        this(name, description, material, Type.ENHANCE);
    }

    public PassiveTalent(@Nonnull String name, @Nonnull String description, @Nonnull Material item, @Nonnull Type type) {
        super(name, description, type);

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

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Passive";
    }
}
