package me.hapyl.fight.game.weapons.ability;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public class DummyAbility extends Ability {
    public DummyAbility(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        return Response.OK;
    }

    public static DummyAbility of(@Nonnull String name, @Nonnull String description) {
        return new DummyAbility(name, description);
    }
}
