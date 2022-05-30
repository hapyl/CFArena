package me.hapyl.fight.game.team;

import com.google.common.collect.Lists;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public enum GameTeam {

    RED(ChatColor.RED, Material.RED_WOOL),
    GREEN(ChatColor.GREEN, Material.GREEN_WOOL),
    BLUE(ChatColor.BLUE, Material.BLUE_WOOL),
    YELLOW(ChatColor.YELLOW, Material.YELLOW_WOOL);

    private final ChatColor color;
    private final Material material;
    private final int maxPlayers;
    private final List<Player> lobbyPlayers; // represents lobby players
    private final List<GamePlayer> players;  // represents actual players in game

    GameTeam(ChatColor color, Material material) {
        this.color = color;
        this.material = material;
        this.maxPlayers = 4;
        this.lobbyPlayers = Lists.newArrayList();
        this.players = Lists.newArrayList();
    }

    /**
     * Adds player to the team if it's not empty.
     *
     * @param player - Player to add.
     * @return true if team is not full and player has been added, false otherwise
     */
    public boolean addToTeam(Player player) {
        if (isFull()) {
            return false;
        }

        final GameTeam oldTeam = getPlayerTeam(player);
        if (oldTeam != null) {
            oldTeam.removeFromTeam(player);
        }

        this.lobbyPlayers.add(player);
        return true;
    }

    // container start
    public void clearAndPopulateTeams() {
        players.clear();
        for (Player player : getLobbyPlayers()) {
            players.add(Shortcuts.getProfile(player).getGamePlayer());
        }
    }

    public void clear() {
        players.clear();
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public boolean isTeamMembers(GamePlayer player, GamePlayer another) {
        return players.contains(player) && players.contains(another);
    }

    public boolean isInTeam(GamePlayer player) {
        return players.contains(player);
    }

    public boolean isTeamAlive(GameTeam team) {
        for (GamePlayer player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTeamDead(GameTeam team) {
        return !isTeamAlive(team);
    }

    // static members
    @Nullable
    public static GameTeam getPlayerTeam(GamePlayer player) {
        for (GameTeam team : values()) {
            if (team.isInTeam(player)) {
                return team;
            }
        }
        return null;
    }

    @Nullable
    public static GameTeam getPlayerTeam(Player player) {
        for (GameTeam team : values()) {
            if (team.isLobbyPlayer(player)) {
                return team;
            }
        }
        return null;
    }

    public static void clearAll() {
        for (GameTeam value : values()) {
            value.lobbyPlayers.clear();
            value.players.clear();
        }
    }

    public static String[] valuesStrings() {
        String[] strings = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            strings[i] = values()[i].name().toLowerCase();
        }
        return strings;
    }

    public void emptyTeam(GameTeam team) {
        players.clear();
    }

    // container end
    public boolean isFull() {
        return lobbyPlayers.size() >= maxPlayers;
    }

    public boolean isLobbyPlayer(Player player) {
        return lobbyPlayers.contains(player);
    }

    public void emptyTeam() {
        lobbyPlayers.clear();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public List<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public Player getLobbyPlayer(int index) {
        return index >= lobbyPlayers.size() ? null : lobbyPlayers.get(index);
    }

    public String getName() {
        return Chat.capitalize(name());
    }

    public void removeFromTeam(Player player) {
        lobbyPlayers.remove(player);
    }
}
