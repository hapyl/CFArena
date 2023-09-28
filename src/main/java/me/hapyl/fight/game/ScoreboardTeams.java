package me.hapyl.fight.game;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.ProfileDisplay;
import me.hapyl.fight.game.team.GameTeam;
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

    public void populateInGame(Player other) {
        if (GameTeam.isTeammate(other, player)) {
            LocalTeam.GAME_ALLY.fetchTeam(player, other);
        }
        else {
            LocalTeam.GAME_ENEMY.fetchTeam(player, other);
        }
    }

    public void populate(boolean toLobby) {
        if (toLobby) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                LocalTeam.LOBBY.fetchTeam(player, other);
            }

        }
        else {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (player == other) {
                    continue; // Don't create team for self
                }

                if (GameTeam.isTeammate(player, other)) {
                    LocalTeam.GAME_ALLY.fetchTeam(player, other);
                }
                else {
                    LocalTeam.GAME_ENEMY.fetchTeam(player, other);
                }
            }
        }
    }

    public static void updateAll() {
        final boolean gameInProgress = Manager.current().isGameInProgress();

        for (Player player : Bukkit.getOnlinePlayers()) {
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                continue;
            }

            profile.getScoreboardTeams().populate(!gameInProgress);
        }
    }

    private enum LocalTeam {
        LOBBY {
            @Override
            public void prepare(Team team, Player player, Player other) {
                final PlayerProfile profile = PlayerProfile.getProfile(other);

                if (profile == null) {
                    throw new IllegalStateException("team creation too soon!");
                }

                final ProfileDisplay display = profile.getDisplay();
                final String displayName = display.getFormat();

                team.setPrefix(displayName.substring(0, Math.min(displayName.length(), 64)));
                team.setColor(display.getColor());

                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }
        },

        GAME_ENEMY {
            @Override
            public void prepare(Team team, Player player, Player other) {
                team.setColor(ChatColor.RED);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        },

        GAME_ALLY {
            @Override
            public void prepare(Team team, Player player, Player other) {
                team.setColor(ChatColor.GREEN);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            }
        };

        @Nonnull
        public Team fetchTeam(Player player, Player other) {
            final Team team = getOrCreateTeam(player, name() + "-" + other.getName());

            prepare(team, player, other);
            team.addEntry(other.getName());
            return team;
        }

        protected void prepare(Team team, Player player, Player other) {
            throw new IllegalStateException();
        }

        public static Team getOrCreateTeam(Player player, String name) {
            final Scoreboard scoreboard = player.getScoreboard();
            Team team = scoreboard.getTeam("%" + name);

            if (team == null) {
                team = scoreboard.registerNewTeam("%" + name);
            }

            return team;
        }

    }

}
