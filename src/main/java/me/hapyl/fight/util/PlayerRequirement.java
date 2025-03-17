package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface PlayerRequirement {

    @Nonnull
    String name();

    boolean hasRequirements(@Nonnull Player player);

    @Nonnull
    default String getRequirementsString(@Nonnull Player player) {
        final boolean hasRequirements = hasRequirements(player);

        return "%s %s".formatted(BukkitUtils.checkmark(hasRequirements), name());
    }

    @Nonnull
    static PlayerRequirement of(@Nonnull String name, @Nonnull Function<Player, Boolean> fn) {
        return new PlayerRequirement() {
            @Nonnull
            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean hasRequirements(@Nonnull Player player) {
                return fn.apply(player);
            }
        };
    }

}
