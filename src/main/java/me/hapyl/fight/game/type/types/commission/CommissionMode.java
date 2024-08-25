package me.hapyl.fight.game.type.types.commission;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.type.GameType;

import javax.annotation.Nonnull;

public class CommissionMode extends GameType {

    public CommissionMode() {
        super("Commission", 99999);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return false;
    }

}
