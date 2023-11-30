package me.hapyl.fight.util.collection.automap;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.collection.player.ConcurrentPlayerMap;

import javax.annotation.Nonnull;

// TODO (hapyl): 025, Nov 25: A map that is designed to "automatically" clear
public class PlayerGameMap<E extends MapElement> extends ConcurrentPlayerMap<E> implements MapElement {

    @Override
    public final void onDeath(@Nonnull GamePlayer player) {
        final E e = remove(player);

        if (e != null) {
            e.onDeath(player);
        }
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {
        values().forEach(e -> e.onStop(player));
    }

    @Override
    public final void onStop() {
        values().forEach(MapElement::onStop);
        clear();
    }
}
