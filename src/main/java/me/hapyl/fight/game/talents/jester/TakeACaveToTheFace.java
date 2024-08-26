package me.hapyl.fight.game.talents.jester;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;

public class TakeACaveToTheFace extends Talent {
    public TakeACaveToTheFace(@Nonnull Key key) {
        super(key, "Take a Cake to the Face");


    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
