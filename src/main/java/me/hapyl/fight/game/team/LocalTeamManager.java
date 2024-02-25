package me.hapyl.fight.game.team;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.ProfileDisplay;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.fight.util.Ticking;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

public class LocalTeamManager implements Ticking {

    private final PlayerProfile profile;

    public LocalTeamManager(@Nonnull PlayerProfile profile) {
        this.profile = profile;

        Manager.current().forEachProfile(this::getTeam);
        updateAll(!Manager.current().isGameInProgress());
    }

    @Nonnull
    public Team getTeam(@Nonnull PlayerProfile profile) {
        final Player player = profile.getPlayer();

        final Scoreboard scoreboard = this.profile.getPlayer().getScoreboard();

        final String teamName = "zzz-cf-%s".formatted(player.getName()); // force to be last on TAB so custom tab can override it

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(player.getName());
            team.addEntry(this.profile.getPlayer().getName()); // for name visibility
        }

        return team;
    }

    public void updateAll(@Nonnull LocalTeamState state) {
        Manager.current().forEachProfile(profile -> {
            state.update(getTeam(profile), profile.getPlayer());
        });
    }

    @Override
    public void tick() {
        Manager.current().forEachProfile(profile -> {
            final Player player = profile.getPlayer();
            final Team team = getTeam(profile);

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
        Manager.current().forEachProfile(profile -> {
            final Team team = getTeam(profile);
            final Player player = profile.getPlayer();

            if (GameTeam.isTeammate(this.profile.getEntry(), profile.getEntry())) {
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

    public static void updateAll() {
        final boolean gameInProgress = Manager.current().isGameInProgress();

        Manager.current().forEachProfile(profile -> {
            final LocalTeamManager teamManager = profile.getLocalTeamManager();

            teamManager.getTeam(profile);
            teamManager.updateAll(!gameInProgress);
        });

        // Update for self as well
        //PlayerProfile.getProfileOptional(player).ifPresent(profile -> {
        //    profile.getLocalTeamManager().updateAll(!gameInProgress);
        //});
    }

}
