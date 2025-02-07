package me.hapyl.fight.game.talents.jester;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class TakeACakeToTheFace extends Talent {
    public TakeACakeToTheFace(@Nonnull Key key) {
        super(key, "Take a Cake to the Face");
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
