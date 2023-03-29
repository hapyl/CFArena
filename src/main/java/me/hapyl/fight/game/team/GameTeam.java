package me.hapyl.fight.game.team;

import com.google.common.collect.Lists;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.util.SmallCaps;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public enum GameTeam {

    RED(ChatColor.RED, Material.RED_WOOL),
    GREEN(ChatColor.GREEN, Material.GREEN_WOOL),
    BLUE(ChatColor.BLUE, Material.BLUE_WOOL),
    YELLOW(ChatColor.YELLOW, Material.YELLOW_WOOL),
    GOLD(ChatColor.GOLD, Material.ORANGE_WOOL),
    AQUA(ChatColor.AQUA, Material.CYAN_WOOL),
    PINK(ChatColor.LIGHT_PURPLE, Material.PURPLE_WOOL),
    WHITE(ChatColor.WHITE, Material.WHITE_WOOL),
    ;

    private static final List<GameTeam> TEAMS = Lists.newArrayList();

    static {
        TEAMS.addAll(Arrays.asList(values()));
    }

    private final ChatColor color;
    private final Material material;
    private final int maxPlayers;
    private final List<UUID> lobbyPlayers; // represents lobby players
    private final List<GamePlayer> players;  // represents actual players in game

    GameTeam(ChatColor color, Material material) {
        this.color = color;
        this.material = material;
        this.maxPlayers = 4;
        this.lobbyPlayers = Lists.newArrayList();
        this.players = Lists.newArrayList();
    }

    /**
     * Returns team with the least amount of players.
     *
     * @return team with the least amount of players.
     * @throws IllegalArgumentException if all teams are full
     */
    @Nonnull
    public static GameTeam getSmallestTeam() {
        int minPlayers = 0;
        GameTeam smallestTeam = null;

        for (GameTeam value : values()) {
            final int size = value.getLobbyPlayers().size();
            if (size <= minPlayers || smallestTeam == null) {
                minPlayers = size;
                smallestTeam = value;
            }
        }

        if (smallestTeam == null) {
            throw new IllegalArgumentException("Couldn't find smallest team.");
        }
        return smallestTeam;
    }

    public static List<GameTeam> getPopulatedTeams() {
        final List<GameTeam> populatedTeams = Lists.newArrayList();

        for (GameTeam value : values()) {
            if (value.getLobbyPlayers().size() > 0) {
                populatedTeams.add(value);
            }
        }

        return populatedTeams;
    }

    public static List<GameTeam> getTeams() {
        return TEAMS;
    }

    public static void clearAllPlayers() {
        for (GameTeam value : values()) {
            value.clear();
        }
    }

    public static boolean isTeammate(Player player, Entity other) {
        if (!(other instanceof Player otherPlayer)) {
            return false;
        }

        return isTeammate(player, otherPlayer);
    }

    public static boolean isSelfOrTeammate(Player player, LivingEntity other) {
        if (player == other) {
            return true;
        }

        return other instanceof Player otherPlayer && isTeammate(player, otherPlayer);
    }

    public static boolean isTeammate(Player player, Player other) {
        // Must consider self check as NOT teammate.
        if ((player == null || other == null) || (player == other)) {
            return false;
        }

        final GameTeam teamA = getPlayerTeam(player);
        final GameTeam teamB = getPlayerTeam(other);

        return (teamA != null && teamB != null) && (teamA == teamB);
    }

    public void removePlayer(Player player) {
        lobbyPlayers.remove(player.getUniqueId());
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

        this.lobbyPlayers.add(player.getUniqueId());
        return true;
    }

    // container start
    public void clearAndPopulateTeams() {
        clear();

        for (Player player : getLobbyPlayers()) {
            if (player.isOnline()) {
                players.add(Shortcuts.getProfile(player).getGamePlayer());
            }
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

    public boolean isTeamAlive() {
        for (GamePlayer player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTeamDead() {
        return !isTeamAlive();
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
        return lobbyPlayers.contains(player.getUniqueId());
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

    public void glowTeammates() {
        for (Player player : getLobbyPlayers()) {
            for (Player other : getLobbyPlayers()) {
                if (other == player) {
                    continue;
                }
                Glowing.glowInfinitly(other, ChatColor.GREEN, player);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public List<Player> getLobbyPlayers() {
        final List<Player> list = Lists.newArrayList();
        for (UUID uuid : lobbyPlayers) {
            list.add(Bukkit.getPlayer(uuid));
        }
        return list;
    }

    public Player getLobbyPlayer(int index) {
        final List<Player> list = getLobbyPlayers();
        return index >= list.size() ? null : list.get(index);
    }

    public String getName() {
        return Chat.capitalize(name());
    }

    public void removeFromTeam(Player player) {
        lobbyPlayers.remove(player.getUniqueId());
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
    }

    public String getNameSmallCaps() {
        return SmallCaps.format(getName());
    }

    public String getNameCaps() {
        return color + "&l" + getName().toUpperCase(Locale.ROOT);
    }

    public String getFirstLetterCaps() {
        return color + "&l" + getName().toUpperCase(Locale.ROOT).charAt(0);
    }
}
