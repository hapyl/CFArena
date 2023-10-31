package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.List;

public interface PlayerList extends List<GamePlayer> {

    @Nonnull
    static PlayerArrayList newList() {
        return new PlayerArrayList();
    }

    @Nonnull
    static LinkedPlayerList newLinkedList() {
        return new LinkedPlayerList();
    }

}
