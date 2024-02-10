package me.hapyl.fight.game.gamemode.modes;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.collection.LinkedValue2IntegerReverseMap;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public class Deathmatch extends CFGameMode {

    private final int SCOREBOARD_DISPLAY_LIMIT = 5;

    public Deathmatch() {
        super("Deathmatch", 300);

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
    public void formatScoreboard(Scoreboarder builder, GameInstance instance, GamePlayer gamePlayer) {
        final Player player = gamePlayer.getPlayer();
        final GameTeam playerTeam = gamePlayer.getTeam();
        final Map<GameTeam, Integer> topKills = getTopTeamKills(instance, SCOREBOARD_DISPLAY_LIMIT);

        builder.addLines("", "&6⚔ &l%s: &8(&b🗡 &l%s&8)".formatted(getName(), playerTeam.data.kills));

        final IntInt i = new IntInt(1);
        topKills.forEach((team, kills) -> {
            builder.addLines(BFormat.format(" &e#&l{Position} &f{Name} &b🗡 &l{Kills}", i.get(), formatTeamName(team), kills));
            i.increment();
        });

        for (int j = i.get(); j <= SCOREBOARD_DISPLAY_LIMIT; j++) {
            builder.addLines(" &e...");
        }
    }

    @Override
    public void displayWinners(@Nonnull GameResult result) {
        super.displayWinners(result);

        // TODO (hapyl): 009, Feb 9: Add more data like TOP 5 yeah
    }

    public String formatTeamName(GameTeam team) {
        final StringBuilder builder = new StringBuilder(team.getFirstLetterCaps()).append(" ").append(ChatColor.WHITE);

        int index = 0;
        for (GamePlayer player : team.getPlayers()) {
            final String playerName = player.getName();

            if (index++ != 0) {
                builder.append(", ");
            }

            builder.append(playerName.length() > 6 ? playerName.substring(0, 6) : playerName);
        }

        return builder.toString();
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
        Chat.broadcast(ChatColor.RED + "%s left the game.", player.getName());
        Chat.broadcast(ChatColor.RED + "They may rejoin and continue playing!");
        Chat.broadcast("");
    }

    @Override
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        final String playerName = player.getName();

        if (profile == null) {
            return;
        }

        GamePlayer gamePlayer = CF.getPlayer(player);

        // Player joined while the game is in progress
        if (gamePlayer == null) {
            gamePlayer = profile.createGamePlayer();
            gamePlayer.setState(EntityState.RESPAWNING);
            gamePlayer.resetPlayer();
            gamePlayer.respawnIn(60);

            player.setGameMode(GameMode.SPECTATOR);

            Chat.broadcast("");
            Chat.broadcast(ChatColor.GREEN + "%s joined the game!", playerName);
            Chat.broadcast(ChatColor.GREEN + "They will be playing with you.");
            Chat.broadcast("");
        }
        // Player re-joined
        else {
            gamePlayer.setHandle(player);
            profile.setGamePlayer(gamePlayer);

            Chat.broadcast("");
            Chat.broadcast(ChatColor.GREEN + "%s rejoined the game!", playerName);
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
