package me.hapyl.fight.game.team;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerDisplay;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.ui.UIFormat;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

// FIXME @May 25, 2025 (xanyjl) -> This is actually terrible but I'm scared to touch it
public class LocalTeamManager implements Ticking {
    
    private final PlayerProfile profile;
    
    public LocalTeamManager(@Nonnull PlayerProfile profile) {
        this.profile = profile;
    }
    
    @Nonnull
    public Team getTeam(@Nonnull PlayerProfile profile) {
        final Player player = profile.getPlayer();
        final Scoreboard scoreboard = this.profile.getPlayer().getScoreboard();
        
        final String teamName = "cf_%s".formatted(player.getName());
        
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
                final PlayerDisplay display = profile.display();
                final String displayName = display.toString(PlayerDisplay.Part.LEVEL, PlayerDisplay.Part.STATUS, PlayerDisplay.Part.PREFIX);
                
                team.setPrefix(displayName.substring(0, Math.min(displayName.length(), 64)));
                team.setSuffix("");
                team.setColor(display.getColor().backingColor);
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
        return Manager.current().currentInstanceOrNull() != null;
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
