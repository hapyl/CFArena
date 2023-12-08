package me.hapyl.fight.game.team;

import me.hapyl.fight.util.Resettable;

public class TeamData implements Resettable {

    private final GameTeam team;

    public int kills;
    public int deaths;

    public TeamData(GameTeam team) {
        this.team = team;
    }

    public GameTeam getTeam() {
        return team;
    }

    @Override
    public void reset() {
        kills = 0;
        deaths = 0;
    }
}
