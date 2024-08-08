package me.hapyl.fight.game.talents.echo;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class EchoTalent extends Talent {


    public EchoTalent() {
        super("Echo");

        setDescription("""
                Create an echo at your current location.

                Use again while deployed to change form between echoes.
                """);

    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
