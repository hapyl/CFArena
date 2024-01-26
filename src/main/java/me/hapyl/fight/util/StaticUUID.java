package me.hapyl.fight.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface StaticUUID {

    StaticUUID HAPYL = new StaticUUID() {
        static final UUID uuid = UUID.fromString("b58e578c-8e36-4789-af50-1ee7400307c0");

        @Nonnull
        @Override
        public UUID getUUID() {
            return uuid;
        }
    };

    @Nonnull
    UUID getUUID();

    default boolean matches(@Nonnull UUID uuid) {
        return getUUID().equals(uuid);
    }

    default boolean matches(@Nonnull Player player) {
        return matches(player.getUniqueId());
    }

    default boolean matches(@Nonnull OfflinePlayer player) {
        return matches(player.getUniqueId());
    }

}
