package me.hapyl.fight.game.team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public enum LocalTeamState {

    LOBBY {
        @Override
        public void update(@Nonnull Team team, @Nonnull Player player) {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

            team.setCanSeeFriendlyInvisibles(false);
        }
    },

    GAME_ALLY {
        @Override
        public void update(@Nonnull Team team, @Nonnull Player player) {
            team.setPrefix("");

            team.setColor(ChatColor.GREEN);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

            team.setCanSeeFriendlyInvisibles(true);
        }
    },

    GAME_ENEMY {
        @Override
        public void update(@Nonnull Team team, @Nonnull Player player) {
            team.setColor(ChatColor.RED);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

            team.setCanSeeFriendlyInvisibles(true);
        }
    };

    public void update(@Nonnull Team team, @Nonnull Player player) {
        throw new IllegalStateException("Override me!");
    }

}
