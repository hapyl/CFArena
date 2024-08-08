package me.hapyl.fight.game.talents.nyx;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class WitherImitation extends Talent {

    public WitherImitation() {
        super("Wither Imitation");
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        player.getAttributes().decreaseTemporary(Temper.COMMAND, AttributeType.HEIGHT, 0.1, 40, player);
        return Response.OK;
    }
}
