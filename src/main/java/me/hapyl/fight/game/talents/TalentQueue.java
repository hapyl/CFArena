package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * Stores the last talents used by a player.
 */
public class TalentQueue {

    public static final TalentQueue EMPTY = new TalentQueue(null) {
        @Nonnull
        @Override
        public GamePlayer getPlayer() {
            throw new IllegalStateException("empty talent queue");
        }
    };

    private final GamePlayer player;
    private final LinkedList<Talent> queue;

    public TalentQueue(GamePlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public void add(Talent talent) {
        this.queue.offerLast(talent);
    }

    /**
     * Returns a copy of linked list with last N talents.
     *
     * @param n - Number of talents to return.
     * @return copy of linked list with last N talents.
     */
    @Nonnull
    public LinkedList<Talent> getLast(int n) {
        if (queue.size() == 0) {
            return new LinkedList<>();
        }

        return new LinkedList<>(this.queue.subList(Math.max(0, this.queue.size() - n), this.queue.size()));
    }

    @Nullable
    public Talent getLastUsedTalent() {
        return queue.peekLast();
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}
