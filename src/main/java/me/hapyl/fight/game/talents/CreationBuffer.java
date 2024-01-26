package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public final class CreationBuffer implements Iterable<Creation> {

    private final Buffer<Creation> buffer;
    private final GamePlayer player;

    public CreationBuffer(@Nonnull GamePlayer player, int bufferSize) {
        this.player = player;
        this.buffer = new Buffer<>(bufferSize) {
            @Override
            public void unbuffered(@Nonnull Creation creation) {
                creation.onReplace(player);
                creation.remove();
            }
        };
    }

    public GamePlayer getPlayer() {
        return player;
    }

    @Nullable
    public Creation first() {
        return buffer.pollFirst();
    }

    @Nullable
    public Creation peekFirst() {
        return buffer.peekFirst();
    }

    @Nullable
    public Creation last() {
        return buffer.pollLast();
    }

    @Nullable
    public Creation peekLast() {
        return buffer.peekLast();
    }

    public void remove(@Nullable Creation creation) {
        if (creation == null) {
            return;
        }

        creation.remove();
        buffer.remove(creation);
    }

    public void add(@Nonnull Creation creation) {
        creation.create(player);
        buffer.add(creation);
    }

    public void clear() {
        buffer.forEach(Removable::remove);
        buffer.clear();
    }

    public int count() {
        return buffer.size();
    }

    @Override
    public String toString() {
        return "CreationBuffer{" +
                "player=" + player +
                ", buffer=" + buffer +
                '}';
    }

    @Nonnull
    @Override
    public Iterator<Creation> iterator() {
        return buffer.iterator();
    }
}
