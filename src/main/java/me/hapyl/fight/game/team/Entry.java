package me.hapyl.fight.game.team;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a {@link GameTeam} entries.
 * <p>
 * Could either be {@link Player}/{@link GamePlayer} or a {@link Entity}/{@link GameEntity}.
 */
public class Entry {

    private final UUID uuid;
    private final boolean isPlayer;

    private Entry(UUID uuid, boolean isPlayer) {
        this.uuid = uuid;
        this.isPlayer = isPlayer;
    }

    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Entry entry = (Entry) o;
        return Objects.equals(uuid, entry.uuid);
    }

    @Override
    public String toString() {
        final Player player = getPlayer();

        return player != null ? player.getName() : uuid.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public boolean isNotPlayer() {
        return !isPlayer;
    }

    @Nullable
    protected Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Nullable
    protected GamePlayer getGamePlayer() {
        return CF.getPlayer(uuid);
    }

    @Deprecated
    @Nonnull
    public static Entry of(@Nonnull UUID uuid) {
        return new Entry(uuid, false);
    }

    @Nonnull
    public static Entry of(@Nonnull Entity entity) {
        return new Entry(entity.getUniqueId(), entity instanceof Player);
    }

    public static Entry of(@Nonnull GameEntity entity) {
        return new Entry(entity.getUUID(), entity instanceof GamePlayer);
    }

}
