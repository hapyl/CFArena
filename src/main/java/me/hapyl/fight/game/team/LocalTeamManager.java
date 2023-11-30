package me.hapyl.fight.game.team;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.ProfileDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.util.Set;

public class LocalTeamManager {

    private final Player player;

    public LocalTeamManager(@Nonnull Player player) {
        this.player = player;

        for (Player other : getOnlinePlayer()) {
            if (player == other) {
                continue;
            }

            getTeam(other);
        }

        updateAll(!Manager.current().isGameInProgress());
    }

    @Nonnull
    public Team getTeam(Player player) {
        if (this.player == player) {
            throw new IllegalArgumentException("Don't create teams for self!");
        }

        final Team team = getOrCreateTeam(this.player.getScoreboard(), "!cf-" + player.getName());
        team.addEntry(player.getName());

        return team;
    }

    public void updateAll(@Nonnull LocalTeamState state) {
        getOnlinePlayer().forEach(player -> {
            state.update(getTeam(player), player);
        });
    }

    @Nonnull
    public Set<Player> getOnlinePlayer() {
        final Set<Player> players = Sets.newHashSet(Bukkit.getOnlinePlayers());
        players.remove(this.player);

        return players;
    }

    public void tickAll() {
        getOnlinePlayer().forEach(player -> {
            final Team team = getTeam(player);
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                return;
            }

            final ProfileDisplay display = profile.getDisplay();
            final String displayName = display.getFormat();

            team.setPrefix(displayName.substring(0, Math.min(displayName.length(), 64)));
            team.setColor(display.getColor().bukkitChatColor);
        });
    }

    public void updateAll(boolean toLobby) {
        if (toLobby) {
            updateAll(LocalTeamState.LOBBY);
        }
        else {
            updateAllInGame();
        }
    }

    public void updateAllInGame() {
        getOnlinePlayer().forEach(player -> {
            final Team team = getTeam(player);
            if (GameTeam.isTeammate(this.player, player)) {
                LocalTeamState.GAME_ALLY.update(team, player);
            }
            else {
                LocalTeamState.GAME_ENEMY.update(team, player);
            }
        });
    }

    public static void updateAll(Player player) {
        final boolean gameInProgress = Manager.current().isGameInProgress();

        for (Player other : Bukkit.getOnlinePlayers()) {
            final PlayerProfile profile = PlayerProfile.getProfile(other);

            if (player == other || profile == null) {
                continue;
            }

            final LocalTeamManager teamManager = profile.getLocalTeamManager();

            teamManager.getTeam(player);
            teamManager.updateAll(!gameInProgress);
        }
    }

    @Nonnull
    public static Team getOrCreateTeam(@Nonnull Scoreboard scoreboard, @Nonnull String name) {
        Team team = scoreboard.getTeam("!" + name);

        if (team == null) {
            team = scoreboard.registerNewTeam("!" + name);
        }

        return team;
    }
}
