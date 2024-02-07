package me.hapyl.fight.game.competetive;

import javax.annotation.Nonnull;

public class TournamentOptions {

    private final Tournament competitive;
    private WinCondition winCondition;
    private int wins;
    private boolean locked;

    public TournamentOptions(Tournament competitive) {
        this.competitive = competitive;
        this.winCondition = WinCondition.BEST_OF;
        this.wins = 3;
    }

    public void setLocked() {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }

    @Nonnull
    public Tournament getCompetitive() {
        return competitive;
    }

    @Nonnull
    public WinCondition getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(@Nonnull WinCondition winCondition) {
        this.winCondition = winCondition;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
