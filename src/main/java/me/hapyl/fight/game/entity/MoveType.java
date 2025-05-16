package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;

public enum MoveType {
    /**
     * Last time player has moved their move or walked.
     */
    MOUSE_OR_KEYBOARD,
    
    /**
     * Last time player has moved at least a little.
     */
    KEYBOARD;

    public String getLastMoved(@Nonnull GamePlayer player) {
        final long timeMillis = System.currentTimeMillis();
        final long lastMoved = player.getLastMoved(this);

        return "%s, -%s".formatted(lastMoved, timeMillis - lastMoved);
    }
}