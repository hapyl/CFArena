package me.hapyl.fight.game.commission;

import me.hapyl.fight.game.entity.commission.CommissionEntityType;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public record Monster(@Nonnull Supplier<CommissionEntityType> type, int level) {

    @Override
    public String toString() {
        return "%s [%s]".formatted(type.get().getKey(), level);
    }
}
