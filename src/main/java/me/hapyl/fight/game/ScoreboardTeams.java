package me.hapyl.fight.game;

import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

// FIXME (hapyl): 031, May 31: lobby has collision for some reason
public class ScoreboardTeams {

    private final Player player;

    public ScoreboardTeams(Player player) {
        this.player = player;
    }

    public void populateInGame(Player other) {
        if (GameTeam.isTeammate(other, player)) {
            LocalTeam.GAME_ALLY.fetchTeam(player, false).addEntry(other.getName());
        }
        else {
            LocalTeam.GAME_ENEMY.fetchTeam(player, false).addEntry(other.getName());
        }
    }

    public void populate(boolean lobby) {
        if (lobby) {
            final Team team = LocalTeam.LOBBY.fetchTeam(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                team.addEntry(online.getName());
            }
        }
        else {
            final Team teamAlly = LocalTeam.GAME_ALLY.fetchTeam(player);
            final Team teamEnemy = LocalTeam.GAME_ENEMY.fetchTeam(player);

            for (Player other : Bukkit.getOnlinePlayers()) {
                final String name = other.getName();

                if (GameTeam.isTeammate(other, player)) {
                    teamAlly.addEntry(name);
                }
                else {
                    teamEnemy.addEntry(name);
                }
            }
        }
    }

    // Updates scoreboard to all
    public static void updateAll() {
        // TODO (hapyl): 002, Jun 2:
    }

    private static Team getOrCreateTeam(Player player, String name) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("%" + name);

        if (team == null) {
            team = scoreboard.registerNewTeam("%" + name);
        }

        return team;
    }

    private enum LocalTeam {
        LOBBY(team -> {
            team.setColor(ChatColor.WHITE);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }),

        GAME_ENEMY(team -> {
            team.setColor(ChatColor.RED);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }),

        GAME_ALLY(team -> {
            team.setColor(ChatColor.GREEN);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        });

        private final Action<Team> action;

        LocalTeam(Action<Team> action) {
            this.action = action;
        }

        @Nonnull
        public Team fetchTeam(Player player) {
            return fetchTeam(player, true);
        }

        @Nonnull
        public Team fetchTeam(Player player, boolean clearEntries) {
            final Team team = getOrCreateTeam(player, name());

            if (clearEntries) {
                for (String entry : team.getEntries()) {
                    team.removeEntry(entry);
                }
            }

            action.use(team);
            return team;
        }

    }

}
