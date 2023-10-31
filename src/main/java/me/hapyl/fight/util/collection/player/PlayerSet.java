package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.Set;

public interface PlayerSet extends Set<GamePlayer> {

    @Nonnull
    static PlayerSet newSet() {
        return new PlayerHashSet();
    }

}
