package me.hapyl.fight.game.gamemode.modes;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Debugger;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;

public class FreeForAll extends CFGameMode {
    public FreeForAll() {
        super("Free for All", 600);
        this.setInfo("One life, one chance to win. Last man standing wins.");
        this.setPlayerRequirements(2);
        this.setMaterial(Material.IRON_SWORD);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        final Set<GameTeam> teams = Sets.newHashSet();

        for (GameTeam team : GameTeam.getTeams()) {
            for (GamePlayer player : team.getPlayers()) {

                final Player playerPlayer = player.getPlayer();
                if (playerPlayer != null) {
                    Debugger.log(playerPlayer.getName());
                    Debugger.log("player.isAlive() = " + player.isAlive());
                    Debugger.log("---");
                }

                if (player.isAlive()) {
                    teams.add(team);
                    break;
                }
            }
        }

        Debugger.log(teams.size());

        return teams.size() == 1;
    }

}
