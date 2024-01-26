package me.hapyl.fight.game.team;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.ProfileDisplay;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.fight.util.Ticking;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.util.Set;

public class LocalTeamManager implements Ticking {

    private final Player player;

    public LocalTeamManager(@Nonnull Player player) {
        this.player = player;

        for (Player other : getOnlinePlayers()) {
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

        final Scoreboard scoreboard = this.player.getScoreboard();
        final String teamName = "!cf-" + player.getName();

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(player.getName());
            team.addEntry(this.player.getName()); // for name visibility
        }

        return team;
    }

    public void updateAll(@Nonnull LocalTeamState state) {
        getOnlinePlayers().forEach(player -> {
            state.update(getTeam(player), player);
        });
    }

    @Nonnull
    public Set<Player> getOnlinePlayers() {
        final Set<Player> players = Sets.newHashSet(Bukkit.getOnlinePlayers());
        players.remove(this.player);

        return players;
    }

    @Override
    public void tick() {
        getOnlinePlayers().forEach(player -> {
            final Team team = getTeam(player);
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                return;
            }

            if (isGameInProgress()) {
                CF.getPlayerOptional(player).ifPresent(gamePlayer -> {
                    team.setSuffix(Chat.format(UIFormat.DIV + gamePlayer.getHealthFormatted()));
                });
            }
            else {
                final ProfileDisplay display = profile.getDisplay();
                final String displayName = display.getFormat();

                team.setPrefix(displayName.substring(0, Math.min(displayName.length(), 64)));
                team.setSuffix("");
                team.setColor(display.getColor().bukkitChatColor);
            }
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
        getOnlinePlayers().forEach(player -> {
            final Team team = getTeam(player);

            if (GameTeam.isTeammate(Entry.of(this.player), Entry.of(player))) {
                LocalTeamState.GAME_ALLY.update(team, player);
            }
            else {
                LocalTeamState.GAME_ENEMY.update(team, player);
            }
        });
    }

    private boolean isGameInProgress() {
        return Manager.current().getGameInstance() != null;
    }

    public static void updateAll(@Nonnull Player player) {
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

        // Update for self as well
        PlayerProfile.getProfileOptional(player).ifPresent(profile -> {
            profile.getLocalTeamManager().updateAll(!gameInProgress);
        });
    }

}
