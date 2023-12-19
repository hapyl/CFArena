package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;

public class TestCastTalent extends Talent {
    public TestCastTalent(@Nonnull String name) {
        super(name);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }

    public void a() {

    }
}
