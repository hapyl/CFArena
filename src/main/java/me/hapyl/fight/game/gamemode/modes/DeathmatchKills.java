package me.hapyl.fight.game.gamemode.modes;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.StatContainer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DeathmatchKills extends CFGameMode {

    private final int killsGoal = 10;

    public DeathmatchKills() {
        super("Kills Deathmatch", 1800);

        setDescription("Free for All death-match when everyone is fighting for kills.__First player to reach &a%s&7 kills wins!".formatted(
                killsGoal));
        setPlayerRequirements(2);
        setMaterial(Material.WITHER_SKELETON_SKULL);

        setAllowRespawn(true);
        setRespawnTime(40);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        for (GamePlayer player : instance.getPlayers().values()) {
            if (player.getStats().getValue(StatContainer.Type.KILLS) >= killsGoal) {
                final GameResult result = instance.getGameResult();
                result.getWinners().add(player);
                result.getWinningTeams().add(player.getTeam());
                return true;
            }
        }

        return false;
    }

    @Override
    public void formatScoreboard(Scoreboarder builder, GameInstance instance, GamePlayer player) {
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
