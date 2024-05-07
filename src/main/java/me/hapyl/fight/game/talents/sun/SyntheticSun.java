package me.hapyl.fight.game.talents.sun;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class SyntheticSun extends Talent {
    public SyntheticSun() {
        super("Synthetic Sun");

        setDescription("""
                Create a {name} in front of you that gradually expands while pulling enemies in.
                                
                After {duration}, explode it and deal lethal damage to enemies in range.
                """);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        return null;
    }
}
