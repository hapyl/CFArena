package me.hapyl.fight.game.commission;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.maps.CommissionLevel;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.type.EnumGameType;

import javax.annotation.Nonnull;

public class CommissionInstance extends GameInstance {
    public CommissionInstance(@Nonnull EnumLevel level) {
        super(EnumGameType.COMMISSION, level);

        if (!(level.getLevel() instanceof CommissionLevel)) {
            throw new IllegalArgumentException("Cannot start commission in non-commission level!");
        }
    }
}
