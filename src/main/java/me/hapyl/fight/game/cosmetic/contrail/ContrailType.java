package me.hapyl.fight.game.cosmetic.contrail;

import javax.annotation.Nonnull;

public record ContrailType(@Nonnull String type, @Nonnull String description) {

    @Nonnull
    public static ContrailType of(@Nonnull String type, @Nonnull String description) {
        return new ContrailType(type, description);
    }

}
