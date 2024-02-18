package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * Stores the last talents used by a player.
 */
public class TalentQueue {

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
     * Returns a copy of the linked list with last N talents.
     *
     * @param n - Number of talents to return.
     * @return copy of a linked list with last N talents.
     */
    @Nonnull
    public LinkedList<Talent> getLast(int n) {
        if (queue.isEmpty()) {
            return new LinkedList<>();
        }

        return new LinkedList<>(this.queue.subList(Math.max(0, this.queue.size() - n), this.queue.size()));
    }

    public boolean checkTalents(Talent... talents) {
        if (talents == null || talents.length == 0) {
            return false;
        }

        final LinkedList<Talent> lastTalents = getLast(talents.length);
        int matches = 0;

        for (Talent talent : talents) {
            final Talent next = lastTalents.poll();
            if (next == null) {
                break;
            }

            if (next.equals(talent)) {
                matches++;
            }
        }

        return matches >= talents.length;
    }

    @Nullable
    public Talent getLastUsedTalent() {
        return queue.peekLast();
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    public void clear() {
        queue.clear();
    }
}
