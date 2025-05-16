package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PassiveTalent extends Talent {

    public PassiveTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);

        setType(TalentType.ENHANCE); // Default passives to enhance because most of them are
        setPoint(0);                 // Passive talents don't regen ultimate
    }

    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        player.sendMessage(Message.ERROR, "Do not execute passive talents!");
        return Response.OK;
    }

    @Override
    public boolean isDisplayAttributes() {
        return false; // This will remove the "Show Attributes" button for passive talents, because most of them don't need it.
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Passive";
    }
}
