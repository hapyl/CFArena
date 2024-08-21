package me.hapyl.fight.game.talents.vampire;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Bloodshift extends Talent {
    public Bloodshift(@Nonnull DatabaseKey key) {
        super(key, "Bloodshift");
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return Response.OK;
    }
}
