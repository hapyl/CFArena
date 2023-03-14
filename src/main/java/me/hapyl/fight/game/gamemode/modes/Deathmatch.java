package me.hapyl.fight.game.gamemode.modes;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.spigotutils.module.chat.Chat;
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
    private final ChatColor[] colors = { ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED };

    public Deathmatch() {
        super("Deathmatch", 300);

        setDescription("Free for All death-match when everyone is fighting for kills. Player with most kills in time limit wins.");
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
    public boolean onStart(@Nonnull GameInstance instance) {
        return false;
    }

    public final LinkedHashMap<GamePlayer, Long> getTopKills(@Nonnull AbstractGameInstance instance, int limit) {
        final Map<GamePlayer, Long> map = getAllKills(instance);
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public final Map<GamePlayer, Long> getAllKills(@Nonnull AbstractGameInstance instance) {
        final Map<GamePlayer, Long> topKills = Maps.newHashMap();

        instance.getPlayers().values().forEach(player -> {
            topKills.put(player, (long) player.getStats().getValue(StatContainer.Type.KILLS));
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
        final GamePlayer gamePlayer = instance.getPlayer(player);
        if (gamePlayer == null || gamePlayer.isSpectator()) {
            return;
        }

        gamePlayer.setHandle(player);
        GamePlayer.getPlayer(player).respawnIn(60);

        Chat.broadcast("");
        Chat.broadcast("&a%s rejoined the game!", player.getName());
        Chat.broadcast("");
    }

    @Override
    public boolean onStop(@Nonnull GameInstance instance) {
        final LinkedHashMap<GamePlayer, Long> topKillsSorted = getTopKills(instance, getAllKills(instance).size());
        final GameResult gameResult = instance.getGameResult();

        long topKills = -1;

        // First player is always a winner
        for (GamePlayer player : topKillsSorted.keySet()) {
            final long kills = topKillsSorted.get(player);

            // Check for first player or other players that have the same kills
            if (topKills == -1 || kills == topKills) {
                topKills = kills;
                gameResult.getWinners().add(player);
                gameResult.getWinningTeams().add(player.getTeam());
            }

        }

        return true;
    }

}
