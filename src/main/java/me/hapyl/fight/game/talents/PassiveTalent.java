package me.hapyl.fight.game.talents;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public class PassiveTalent extends Talent {

    protected PassiveTalent(@Nonnull DatabaseKey key, @Nonnull String name) {
        super(key, name);

        setType(TalentType.ENHANCE); // Default passives to enhance because most of them are
        setPoint(0);                 // Passive talents don't regen ultimate
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
