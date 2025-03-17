package me.hapyl.fight.game.type.types;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.scoreboard.Scoreboarder;
import me.hapyl.eterna.module.util.collection.LinkedValue2IntegerReverseMap;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EntityState;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.game.type.GameType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Deathmatch extends GameType {

    private static final int SCOREBOARD_DISPLAY_LIMIT = 5;

    public Deathmatch(@Nonnull EnumGameType handle) {
        super(handle, "Deathmatch", 300);

        setDescription("""
                Free for All death-match when everyone is fighting for kills.
                
                Player with most kills in time limit wins.
                """);
        setPlayerRequirements(2);
        setMaterial(Material.SKELETON_SKULL);

        setAllowRespawn(true);
        setRespawnTime(40);
    }

    @Override
    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return false;
    }

    @Override
    public void formatScoreboard(@Nonnull Scoreboarder builder, @Nonnull GameInstance instance, @Nonnull GamePlayer player) {
        final GameTeam playerTeam = player.getTeam();
        final Map<GameTeam, Integer> topKills = getTopTeamKills(instance, SCOREBOARD_DISPLAY_LIMIT);

        builder.addLines("&6&l%s &8(&b🗡 &l%s&8)".formatted(nameSmallCaps(), playerTeam.data.kills));

        int position = 1;

        for (Map.Entry<GameTeam, Integer> entry : topKills.entrySet()) {
            final GameTeam team = entry.getKey();
            final Integer kills = entry.getValue();

            builder.addLine(" &f#&l%s &f%s &b🗡 &l%s".formatted(position++, team.formatTeamName(), kills));
        }

        for (int j = position; j <= SCOREBOARD_DISPLAY_LIMIT; j++) {
            builder.addLines(" &f#&l%s &8...".formatted(j));
        }
    }

    @Override
    public void displayWinners(@Nonnull GameResult result) {
        super.displayWinners(result);

        // TODO (hapyl): 009, Feb 9: Add more data like TOP 5 yeah
    }

    public final LinkedHashMap<GameTeam, Integer> getTopTeamKills(@Nonnull IGameInstance instance, int limit) {
        final Map<GameTeam, Integer> map = Maps.newHashMap();

        for (GameTeam team : GameTeam.getPopulatedTeams()) {
            map.put(team, team.data.kills);
        }

        return LinkedValue2IntegerReverseMap.of(map, limit);
    }

    @Override
    public void onLeave(@Nonnull GameInstance instance, @Nonnull Player player) {
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null || gamePlayer.isSpectator()) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        gamePlayer.setState(EntityState.DEAD);

        Chat.broadcast("");
        Chat.broadcast(ChatColor.RED + "%s left the game.".formatted(player.getName()));
        Chat.broadcast(ChatColor.RED + "They may rejoin and continue playing!");
        Chat.broadcast("");
    }

    @Override
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        final PlayerProfile profile = CF.getProfile(player);
        final String playerName = player.getName();

        GamePlayer gamePlayer = CF.getPlayer(player);

        // Player joined while the game is in progress
        if (gamePlayer == null) {
            gamePlayer = profile.createGamePlayer();
            gamePlayer.setState(EntityState.RESPAWNING);
            gamePlayer.resetPlayer();
            gamePlayer.respawnIn(60);

            player.setGameMode(GameMode.SPECTATOR);

            Chat.broadcast("");
            Chat.broadcast(ChatColor.GREEN + "%s joined the game!".formatted(playerName));
            Chat.broadcast(ChatColor.GREEN + "They will be playing with you.");
            Chat.broadcast("");
        }
        // Player re-joined
        else {
            gamePlayer.setHandle(profile, player);

            profile.setGamePlayer(gamePlayer);

            Chat.broadcast("");
            Chat.broadcast(ChatColor.GREEN + "%s rejoined the game!".formatted(playerName));
            Chat.broadcast("");

            gamePlayer.respawnIn(60);
        }
    }

    @Override
    public boolean onStop(@Nonnull GameInstance instance) {
        final LinkedHashMap<GameTeam, Integer> topTeamKills = getTopTeamKills(instance, 1);
        final GameResult gameResult = instance.getGameResult();

        for (GameTeam team : topTeamKills.keySet()) {

            for (GamePlayer player : team.getPlayers()) {
                gameResult.getWinners().add(player);
            }

            gameResult.getWinningTeams().add(team);
            return true; // The first team is always the winner
        }

        return true;
    }

}
