package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TestCastTalent extends Talent {
    public TestCastTalent(@Nonnull String name) {
        super(name);
    }

    @Override
    public Response execute(Player player) {
        return null;
    }

    public void a() {

    }
}
