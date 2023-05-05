package me.hapyl.fight.game.gamemode.modes;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.GameResult;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.scoreboard.Scoreboarder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Deathmatch extends CFGameMode {

    private final int SCOREBOARD_DISPLAY_LIMIT = 5;

    public Deathmatch() {
        super("Deathmatch", 300);

        setDescription("Free for All death-match when everyone is fighting for kills.__Player with most kills in time limit wins.");
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

        builder.addLines("", "&6&lDeathmatch: &f(&b🗡 &l%s&f)".formatted(gamePlayer.getStats().getValue(StatType.KILLS)));

        final IntInt i = new IntInt(1);
        topKills.forEach((team, kills) -> {
            builder.addLines(" %s&e#&l%s &f%s &b🗡 &l%s".formatted(team == playerTeam ? "&a→ " : "", i.get(), formatTeamName(team), kills));
            i.increment();
        });

        for (int j = i.get(); j <= SCOREBOARD_DISPLAY_LIMIT; j++) {
            builder.addLines(" &e...");
        }
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
            map.put(team, team.kills);
        }

        return sortByValue(map, limit);
    }

    @Deprecated
    public final LinkedHashMap<GamePlayer, Integer> getTopKills(@Nonnull IGameInstance instance, int limit) {
        final Map<GamePlayer, Integer> map = getAllKills(instance);
        return sortByValue(map, limit);
    }

    @Deprecated
    public final Map<GamePlayer, Integer> getAllKills(@Nonnull IGameInstance instance) {
        final Map<GamePlayer, Integer> topKills = Maps.newHashMap();

        instance.getPlayers().values().forEach(player -> {
            topKills.put(player, (int) player.getStats().getValue(StatType.KILLS));
        });

        return topKills;
    }

    /**
     * @deprecated Respawn is now handles in GamePlayer
     */
    @Override
    @Deprecated
    public void onDeath(@Nonnull GameInstance instance, @Nonnull GamePlayer player) {
    }

    @Override
    public void onLeave(@Nonnull GameInstance instance, @Nonnull Player player) {
        final GamePlayer gamePlayer = instance.getPlayer(player);
        if (gamePlayer == null || gamePlayer.isSpectator()) {
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        gamePlayer.setDead(true);

        Chat.broadcast("");
        Chat.broadcast("&c%s left the game. Them may rejoin and continue playing!", player.getName());
        Chat.broadcast("");
    }

    @Override
    public void onJoin(@Nonnull GameInstance instance, @Nonnull Player player) {
        final GamePlayer gamePlayer = instance.getOrCreateGamePlayer(player);

        // If player was spectator, don't respawn them
        if (!gamePlayer.isSpectator()) {
            gamePlayer.setHandle(player);
            gamePlayer.respawnIn(60);
        }

        Chat.broadcast("");
        Chat.broadcast("&a%s rejoined the game!", player.getName());
        Chat.broadcast("");
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

    private <K> LinkedHashMap<K, Integer> sortByValue(Map<K, Integer> map, int limit) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}
