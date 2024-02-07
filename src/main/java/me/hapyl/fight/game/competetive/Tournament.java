package me.hapyl.fight.game.competetive;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.team.GameTeam;

import javax.annotation.Nonnull;
import java.util.Map;

public class Tournament {

    private final Map<GameTeam, Integer> teamScore;
    private final TournamentOptions options;

    public Tournament() {
        this.teamScore = Maps.newHashMap();
        this.options = new TournamentOptions(this);

        GameTeam.getPopulatedTeams().forEach(team -> {

        });
    }

    @Nonnull
    public TournamentOptions getOptions() {
        return options;
    }
}
