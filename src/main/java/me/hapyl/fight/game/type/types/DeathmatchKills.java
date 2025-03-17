package me.hapyl.fight.game.type.types;

import me.hapyl.eterna.module.scoreboard.Scoreboarder;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.type.GameType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DeathmatchKills extends GameType {

    protected final int killsGoal;

    DeathmatchKills(EnumGameType handle, String name, int timeLimit, int killsGoal) {
        super(handle, name, timeLimit);
        this.killsGoal = killsGoal;
    }

    public DeathmatchKills(@Nonnull EnumGameType handle) {
        this(handle, "Kills Deathmatch", 1800, 10);

        setDescription("""
                Free for All death-match when everyone is fighting for kills.
                
                First player to reach &a%s&7 kills wins!
                """.formatted(killsGoal));

        setPlayerRequirements(2);
        setMaterial(Material.WITHER_SKELETON_SKULL);

        setAllowRespawn(true);
        setRespawnTime(40);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        for (GameTeam team : GameTeam.getPopulatedTeams()) {
            if (team.data.kills >= killsGoal) {
                final GameResult gameResult = instance.getGameResult();

                for (GamePlayer player : team.getPlayers()) {
                    gameResult.getWinners().add(player);
                }

                gameResult.getWinningTeams().add(team);
                return true;
            }
        }

        return false;
    }

    @Override
    public void formatScoreboard(@Nonnull Scoreboarder builder, @Nonnull GameInstance instance, @Nonnull GamePlayer player) {
        EnumGameType.DEATH_MATCH.getMode().formatScoreboard(builder, instance, player);
    }

    @Override
    public boolean onStop(@Nonnull GameInstance instance) {
        return true;
    }

    @Override
    public void onLeave(@Nonnull GameInstance instance, @Nonnull Player player) {
        EnumGameType.DEATH_MATCH.getMode().onLeave(instance, player);
    }

    @Override
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        EnumGameType.DEATH_MATCH.getMode().onJoin(instance, player);
    }
}
