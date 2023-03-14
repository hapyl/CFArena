package me.hapyl.fight.game.gamemode.modes;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Set;

public class FreeForAll extends CFGameMode {
    public FreeForAll() {
        super("Free for All", 600);

        setDescription("One life, one chance to win. Last man standing wins.");
        setPlayerRequirements(2);
        setMaterial(Material.IRON_SWORD);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        final Set<GameTeam> teams = Sets.newHashSet();

        for (GameTeam team : GameTeam.getTeams()) {
            for (GamePlayer player : team.getPlayers()) {
                if (player.isAlive()) {
                    teams.add(team);
                    break;
                }
            }
        }

        return teams.size() <= 1;
    }

}
