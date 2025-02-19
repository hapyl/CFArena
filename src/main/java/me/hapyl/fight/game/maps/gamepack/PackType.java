package me.hapyl.fight.game.maps.gamepack;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public enum PackType {

    HEALTH("health", "heal", "hp"),
    CHARGE("charge", "ult", "ultimate");

    private final Set<String> aliases;

    PackType(@Nonnull String... aliases) {
        this.aliases = Sets.newHashSet(aliases);
    }

    @Nullable
    public static PackType of(@Nonnull String name) {
        for (PackType pack : values()) {
            if (pack.aliases.contains(name.toLowerCase())) {
                return pack;
            }
        }

        return null;
    }

}
