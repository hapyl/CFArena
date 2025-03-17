package me.hapyl.fight.game.type.types.commission;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.type.GameType;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class CommissionGameType extends GameType implements Listener {

    public CommissionGameType(@Nonnull EnumGameType handle) {
        super(handle, "Commission", 99999);
    }

    @Override
    public final boolean testWinCondition(@Nonnull GameInstance instance) {
        return false;
    }

    @Override
    public final void displayWinners(@Nonnull GameResult result) {
    }

    @Override
    public final int getTeamRequirements() {
        return 1;
    }

}
