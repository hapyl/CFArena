package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DeathmatchKills extends CFGameMode {

    protected final int killsGoal;

    public DeathmatchKills(String name, int timeLimit, int killsGoal) {
        super(name, timeLimit);
        this.killsGoal = killsGoal;
    }

    public DeathmatchKills() {
        this("Kills Deathmatch", 1800, 10);

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
        Modes.DEATH_MATCH.getMode().formatScoreboard(builder, instance, player);
    }

    @Override
    public boolean onStop(@Nonnull GameInstance instance) {
        return true;
    }

    @Override
    public void onLeave(@Nonnull GameInstance instance, @Nonnull Player player) {
        Modes.DEATH_MATCH.getMode().onLeave(instance, player);
    }

    @Override
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        Modes.DEATH_MATCH.getMode().onJoin(instance, player);
    }
}
