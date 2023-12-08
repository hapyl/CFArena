package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.team.GameTeam;
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
