package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.Map;

public interface PlayerMap<V> extends Map<GamePlayer, V> {

    @Nonnull
    static <V> PlayerHashMap<V> newMap() {
        return new PlayerHashMap<>();
    }

    @Nonnull
    static <V> ConcurrentPlayerMap<V> newConcurrentMap() {
        return new ConcurrentPlayerMap<>();
    }

    @Nonnull
    static <V> LinkedPlayerMap<V> newLinkedMap() {
        return new LinkedPlayerMap<>();
    }

}
