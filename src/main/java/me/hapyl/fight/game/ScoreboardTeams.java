package me.hapyl.fight.game;

import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class ScoreboardTeams {

    private final Player player;

    public ScoreboardTeams(Player player) {
        this.player = player;
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

            for (Player player : Bukkit.getOnlinePlayers()) {
                final String name = player.getName();

                if (GameTeam.isTeammate(player, this.player)) {
                    teamAlly.addEntry(name);
                }
                else {
                    teamEnemy.addEntry(name);
                }
            }
        }
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
            final Team team = getOrCreateTeam(player, name());

            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }

            action.use(team);
            return team;
        }
    }

}
