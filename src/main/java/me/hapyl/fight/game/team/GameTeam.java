package me.hapyl.fight.game.team;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
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
    WHITE(ChatColor.WHITE, Material.WHITE_WOOL);

    private static final List<GameTeam> TEAMS = Lists.newArrayList();

    static {
        TEAMS.addAll(Arrays.asList(values()));
    }

    private final ChatColor color;
    private final Material material;
    private final int maxPlayers;
    private final List<UUID> members;   // represents lobby players

    public int kills;
    public int deaths;

    GameTeam(ChatColor color, Material material) {
        this.color = color;
        this.material = material;
        this.maxPlayers = 4;
        this.members = Lists.newArrayList();
    }

    public void removePlayer(Player player) {
        members.remove(player.getUniqueId());
    }

    /**
     * Adds player to the team if it's not empty.
     *
     * @param player - Player to add.
     * @return true if the team is not full and player has been added, false otherwise
     */
    public boolean addMember(Player player) {
        if (isFull()) {
            return false;
        }

        final GameTeam oldTeam = getPlayerTeam(player);
        if (oldTeam != null) {
            oldTeam.removeMember(player);
        }

        this.members.add(player.getUniqueId());
        return true;
    }

    /**
     * Returns PLAYER from current game instance that are in the team; or empty list is no players or no game instance.
     *
     * @return list of players in the team
     */
    public List<GamePlayer> getPlayers() {
        final List<GamePlayer> players = Lists.newArrayList();
        final IGameInstance instance = Manager.current().getCurrentGame();

        if (!instance.isReal()) {
            return players;
        }

        for (GamePlayer gamePlayer : instance.getPlayers().values()) {
            if (members.contains(gamePlayer.getUUID())) {
                players.add(gamePlayer);
            }
        }

        return players;
    }

    public boolean isTeamMembers(GamePlayer player, GamePlayer another) {
        return members.contains(player.getUUID()) && members.contains(another.getUUID());
    }

    public boolean isMember(GamePlayer player) {
        return members.contains(player.getUUID());
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public boolean isTeamAlive() {
        for (GamePlayer player : getPlayers()) {
            if (player.isAlive()) {
                return true;
            }
        }

        return false;
    }

    public boolean isTeamDead() {
        return !isTeamAlive();
    }

    public boolean isFull() {
        return members.size() >= maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ChatColor getColor() {
        return color;
    }

    public void onStop() {
        kills = 0;
        deaths = 0;
    }

    public void glowTeammates() {
        final List<Player> players = getPlayersAsPlayers();

        for (Player player : players) {
            for (Player other : players) {
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

    @Nonnull
    public List<Player> getPlayersAsPlayers() {
        final List<Player> list = Lists.newArrayList();
        for (UUID uuid : members) {
            list.add(Bukkit.getPlayer(uuid));
        }
        return list;
    }

    @Nullable
    public Player getLobbyPlayer(int index) {
        final List<Player> list = getPlayersAsPlayers();
        return index >= list.size() ? null : list.get(index);
    }

    public String getName() {
        return Chat.capitalize(name());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
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

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public List<String> listMembers() {
        final List<String> list = Lists.newArrayList();

        for (UUID member : members) {
            list.add(member.toString());
        }

        return list;
    }

    // static members

    /**
     * Returns team with the least number of players.
     *
     * @return team with the least number of players.
     * @throws IllegalArgumentException if all teams are full
     */
    @Nonnull
    public static GameTeam getSmallestTeam() {
        int minPlayers = 0;
        GameTeam smallestTeam = null;

        for (GameTeam value : values()) {
            final int size = value.getPlayersAsPlayers().size();
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

    public static void removeOfflinePlayers() {
        for (GameTeam team : values()) {
            team.members.removeIf(uuid -> {
                final boolean tempRemove = Bukkit.getPlayer(uuid) == null;

                if (tempRemove) {
                    Debug.info("removed %s from a team because they are no longer online", uuid);
                }

                return tempRemove;
            });
        }
    }

    /**
     * Gets a list of teams where there is at least one player.
     *
     * @return list of populated teams.
     */
    public static List<GameTeam> getPopulatedTeams() {
        final List<GameTeam> populatedTeams = Lists.newArrayList();

        for (GameTeam team : values()) {
            if (team.getPlayersAsPlayers().size() > 0) {
                populatedTeams.add(team);
            }
        }

        return populatedTeams;
    }

    public static List<GameTeam> getTeams() {
        return TEAMS;
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
        // Must consider self-check as NOT teammate.
        if ((player == null || other == null) || (player == other)) {
            return false;
        }

        final GameTeam teamA = getPlayerTeam(player);
        final GameTeam teamB = getPlayerTeam(other);

        return (teamA != null && teamB != null) && (teamA == teamB);
    }

    @Nullable
    public static GameTeam getPlayerTeam(@Nullable GamePlayer player) {
        if (player == null) {
            return null;
        }

        for (GameTeam team : values()) {
            if (team.isMember(player)) {
                return team;
            }
        }
        return null;
    }

    @Nullable
    public static GameTeam getPlayerTeam(Player player) {
        return getPlayerTeam(player.getUniqueId());
    }

    @Nullable
    public static GameTeam getPlayerTeam(UUID uuid) {
        for (GameTeam team : values()) {
            if (team.members.contains(uuid)) {
                return team;
            }
        }

        return null;
    }

    @Nullable
    public static GameTeam getPlayerLobbyTeam(Player player) {
        for (GameTeam team : values()) {
            if (team.members.contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public static String[] valuesStrings() {
        String[] strings = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            strings[i] = values()[i].name().toLowerCase();
        }
        return strings;
    }

    public static void addMemberIfNotInTeam(Player player) {
        if (getPlayerTeam(player) == null) {
            getSmallestTeam().addMember(player);
        }
    }
}
