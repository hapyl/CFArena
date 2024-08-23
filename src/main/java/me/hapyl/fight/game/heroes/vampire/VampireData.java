package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;

public class VampireData extends PlayerData {

    private VampireState state;

    public VampireData(GamePlayer player) {
        super(player);

        this.state = VampireState.DAMAGE;
    }

    @Nonnull
    public VampireState getState() {
        return state;
    }

    public void setState(@Nonnull VampireState state) {
        this.state = state;
    }

    @Override
    public void remove() {
    }

}
