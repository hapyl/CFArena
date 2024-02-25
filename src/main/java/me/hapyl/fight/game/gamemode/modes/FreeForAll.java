package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Comparators;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.List;

public class FreeForAll extends CFGameMode {
    public FreeForAll() {
        super("Free for All", 600);

        setDescription("""
                One life, one chance to win.
                                
                Last man standing wins.
                """);
        setPlayerRequirements(2);
        setMaterial(Material.IRON_SWORD);
    }

    @Override
    public void formatScoreboard(@Nonnull Scoreboarder builder, @Nonnull GameInstance instance, @Nonnull GamePlayer player) {
        final List<GameTeam> teams = GameTeam.getTeams();

        builder.addLine("&c⚔ &6&lFree for All");

        teams.removeIf(team -> !team.hasAnyPlayers());
        teams.sort(Comparators.comparingBool(GameTeam::isTeamAlive));

        teams.forEach(team -> {
            final String teamName = team.formatTeamName();

            if (team.isTeamAlive()) {
                builder.addLine(" &a● " + teamName);
            }
            else {
                builder.addLine(" &c❌ %s &c&m%s".formatted(team.getFirstLetterCaps(), team.formatTeamMembers()));
            }
        });
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        final List<GameTeam> teams = GameTeam.getTeams();

        teams.removeIf(team -> {
            final List<GamePlayer> players = team.getPlayers();

            for (GamePlayer player : players) {
                if (player.isAlive()) {
                    return false;
                }
            }

            return true;
        });

        return teams.size() <= 1;
    }

}
