package me.hapyl.fight.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface StaticUUID {

    StaticUUID HAPYL = new SimpleUUID("b58e578c-8e36-4789-af50-1ee7400307c0");
    StaticUUID DIDEN = new SimpleUUID("491c1d9a-357f-4a98-bd24-4ddbeb8555b0");
    StaticUUID SDIMAS = new SimpleUUID("a7ed32f7-f5a4-4abe-a14e-62cdeea42f3b");

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

    class SimpleUUID implements StaticUUID {
        private final UUID uuid;

        public SimpleUUID(String string) {
            this.uuid = UUID.fromString(string);
        }

        @Nonnull
        @Override
        public UUID getUUID() {
            return uuid;
        }
    }

}
