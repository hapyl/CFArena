package me.hapyl.fight.game.team;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.reflect.glow.Glowing;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.Selectable;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.KeyedEnum;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.Lifecycle;
import me.hapyl.fight.util.SmallCapsDescriber;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Teams are capable of holding members, such as {@link Player} and {@link me.hapyl.fight.game.entity.LivingGameEntity}.
 * <p>
 * The players are limited to {@link #maxPlayers}, but the entities are unlimited.
 * The entities will also be cleared upon {@link #onStop()}
 */
public enum GameTeam implements Described, SmallCapsDescriber, Selectable, Lifecycle, KeyedEnum {

    RED(ChatColor.RED, Material.RED_BANNER),
    GREEN(ChatColor.GREEN, Material.GREEN_BANNER),
    BLUE(ChatColor.BLUE, Material.BLUE_BANNER),
    YELLOW(ChatColor.YELLOW, Material.YELLOW_BANNER),
    GOLD(ChatColor.GOLD, Material.ORANGE_BANNER),
    AQUA(ChatColor.AQUA, Material.CYAN_BANNER),
    PINK(ChatColor.LIGHT_PURPLE, Material.PURPLE_BANNER),
    WHITE(ChatColor.WHITE, Material.WHITE_BANNER),

    // Black team is used as a last resort if all other teams are full
    @Deprecated
    BLACK(ChatColor.BLACK, Material.BLACK_BANNER) {
        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public boolean isAllowJoin() {
            return false;
        }
    };

    private static final List<GameTeam> validTeams = Lists.newArrayList();
    private static final int AVERAGE_NICKNAME_LENGTH = 9;
    private static String[] strings;

    static {
        for (GameTeam team : values()) {
            if (team == BLACK) {
                continue;
            }

            validTeams.add(team);
        }
    }

    public final TeamData data;

    private final ChatColor color;
    private final Material material;
    private final int maxPlayers;
    private final List<Entry> members;
    private final String name;
    private final String flag;
    private final String smallCaps;
    private final org.bukkit.Color bukkitColor;

    private int playerCount;

    GameTeam(ChatColor color, Material material) {
        this.color = color;
        this.material = material;
        this.maxPlayers = 4;
        this.members = Lists.newArrayList();
        this.name = Chat.capitalize(this);
        this.flag = color + "&l🏴";
        this.smallCaps = toSmallCaps(this);
        this.data = new TeamData(this);

        // Get bukkit color
        final java.awt.Color javaColor = color.asBungee().getColor();
        this.bukkitColor = org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
    }

    public void addEntry(@Nonnull Entry entry) {
        if (entry.isPlayer()) {
            if (isFull()) {
                sendMessage(entry, Color.ERROR + "Could not join the team because it's full!");
                return;
            }

            if (members.contains(entry)) {
                sendMessage(entry, Color.ERROR + "You are already in this team!");
                return;
            }

            final GameTeam entryTeam = getEntryTeam(entry);

            if (entryTeam != null) {
                entryTeam.removeEntry(entry);
            }

            sendMessage(entry, Color.SUCCESS + "Joined {} team!", getNameCaps() + Color.SUCCESS);
            playerCount++;
        }

        members.add(entry);
    }

    public void removeEntry(@Nonnull Entry entry) {
        members.remove(entry);

        if (entry.isPlayer()) {
            playerCount--;
        }
    }

    public boolean isEntry(@Nonnull Entry entry) {
        return members.contains(entry);
    }

    @Nonnull
    public List<GamePlayer> getPlayers() {
        final List<GamePlayer> players = Lists.newArrayList();
        final GameInstance instance = Manager.current().getGameInstance();

        if (instance == null) {
            return players;
        }

        members.forEach(entry -> {
            final GamePlayer gamePlayer = entry.getGamePlayer();

            if (gamePlayer == null) {
                return;
            }

            players.add(gamePlayer);
        });

        return players;
    }

    @Nonnull
    public String getFlagColored() {
        return flag;
    }

    public boolean isTeammates(@Nonnull Entry entry, @Nonnull Entry other) {
        return isEntry(entry) && isEntry(other);
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
        return playerCount >= maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Nonnull
    public ChatColor getColor() {
        return color;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        data.reset();

        // Remove non-player entities from the team
        members.removeIf(Entry::isNotPlayer);
    }

    public void glowTeammates() {
        final List<Player> players = getBukkitPlayers();

        for (Player player : players) {
            for (Player other : players) {
                if (other == player) {
                    continue;
                }

                Glowing.glowInfinitely(other, ChatColor.GREEN, player);
            }
        }
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Nonnull
    public List<Player> getBukkitPlayers() {
        final List<Player> list = Lists.newArrayList();

        members.forEach(entry -> {
            final Player player = entry.getPlayer();

            if (player == null) {
                return;
            }

            list.add(player);
        });

        return list;
    }

    @Nullable
    public Player getBukkitPlayer(int index) {
        final List<Player> list = getBukkitPlayers();

        return index >= list.size() ? null : list.get(index);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "A team.";
    }

    @Nonnull
    @Override
    public String getNameSmallCaps() {
        return smallCaps;
    }

    @Nonnull
    public String getNameCaps() {
        return color + "&l" + getName().toUpperCase();
    }

    @Nonnull
    public String getFirstLetterCaps() {
        return color + "&l" + getName().toUpperCase().charAt(0);
    }

    @Nonnull
    public String getNameSmallCapsColorized() {
        return color + getNameSmallCaps();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    @Nonnull
    public List<Entry> listEntries() {
        return Lists.newArrayList(members);
    }

    @Override
    public boolean isSelected(@Nonnull Player player) {
        return isEntry(Entry.of(player));
    }

    @Override
    public void select(@Nonnull Player player) {
        addEntry(Entry.of(player));
    }

    @Override
    public String toString() {
        return name() + "(" + playerCount + ") " + members;
    }

    public boolean isAllowJoin() {
        return true;
    }

    @Nonnull
    public org.bukkit.Color getColorAsBukkitColor() {
        return bukkitColor;
    }

    public void glowEntity(@Nonnull Entity entity) {
        Glowing.glowInfinitely(entity, ChatColor.GREEN, getBukkitPlayers());
    }

    public void glowEntity(@Nonnull DisplayEntity displayEntity) {
        displayEntity.getEntities().forEach(this::glowEntity);
    }

    @Nonnull
    public String formatTeamName() {
        return getFirstLetterCaps() + " " + ChatColor.WHITE + formatTeamMembers();
    }

    @Nonnull
    public String formatTeamMembers() {
        final StringBuilder builder = new StringBuilder();

        int index = 0;
        for (GamePlayer player : getPlayers()) {
            final String playerName = player.getName();

            if (index++ != 0) {
                builder.append(", ");
            }

            builder.append(playerName.length() > AVERAGE_NICKNAME_LENGTH ? playerName.substring(0, AVERAGE_NICKNAME_LENGTH) : playerName);
        }

        return builder.toString();
    }

    public boolean hasAnyPlayers() {
        for (Entry entry : members) {
            if (entry.getGamePlayer() != null) {
                return true;
            }
        }

        return false;
    }

    private void sendMessage(Entry entry, String string, Object... format) {
        final Player player = entry.getPlayer();

        if (player == null) {
            return;
        }

        Chat.sendMessage(player, getFlagColored() + " " + Chat.bformat(string, format));
    }

    /**
     * Gets the team that this entry is in.
     *
     * @param entry - Entry.
     * @return the team this entry is in.
     */
    @Nullable
    public static GameTeam getEntryTeam(@Nonnull Entry entry) {
        for (GameTeam team : values()) {
            if (team.isEntry(entry)) {
                return team;
            }
        }

        return null;
    }

    /**
     * Returns team with the least number of players or null if all teams are full.
     *
     * @return team with the least number of players or null if all teams are full.
     */
    @Nonnull
    public static GameTeam getSmallestTeam() {
        TreeMap<Integer, Set<GameTeam>> teamsBySize = new TreeMap<>();

        for (GameTeam team : validTeams) {
            teamsBySize.compute(team.playerCount, Compute.setAdd(team));
        }

        for (Integer size : teamsBySize.keySet()) {
            final GameTeam randomTeam = CollectionUtils.randomElement(teamsBySize.get(size));

            if (randomTeam != null && !randomTeam.isFull()) {
                return randomTeam;
            }

            break;
        }

        return BLACK;
    }

    public static void removeOfflinePlayers() {
        for (GameTeam team : values()) {
            team.members.removeIf(entry -> {
                boolean isRemove = entry.isPlayer() && entry.getPlayer() == null;

                if (isRemove) {
                    team.playerCount--;
                }

                return isRemove;
            });
        }
    }

    /**
     * Gets a list of teams where there is at least one player.
     *
     * @return list of populated teams.
     */
    @Nonnull
    public static List<GameTeam> getPopulatedTeams() {
        final List<GameTeam> populatedTeams = Lists.newArrayList();

        for (GameTeam team : values()) {
            if (team.playerCount > 0) {
                populatedTeams.add(team);
            }
        }

        return populatedTeams;
    }

    /**
     * Gets a copy of all teams.
     *
     * @return a copy of all teams.
     */
    @Nonnull
    public static List<GameTeam> getTeams() {
        return Lists.newArrayList(validTeams);
    }

    /**
     * Checks whenever two entries are teammates.
     * <p>
     * Special cases:
     * <ul>
     *     <li>Checking for itself considered as not teammate.</li>
     * </ul>
     *
     * @param entry - First entry.
     * @param other - Second entry.
     * @return true if two entries are teammates.
     */
    public static boolean isTeammate(@Nonnull Entry entry, @Nonnull Entry other) {
        if (entry.equals(other)) {
            return false;
        }

        final GameTeam teamA = getEntryTeam(entry);
        final GameTeam teamB = getEntryTeam(other);

        return (teamA != null && teamB != null) && (teamA == teamB);
    }

    /**
     * Checks whenever two entries are the same or are teammates.
     *
     * @param entry - First entry.
     * @param other - Second entry.
     * @return true if two entries are the same or are teammates.
     */
    public static boolean isSelfOrTeammate(@Nonnull Entry entry, @Nonnull Entry other) {
        return entry.equals(other) || isTeammate(entry, other);
    }

    public static String[] valuesStrings() {
        if (strings == null) {
            strings = new String[validTeams.size()];

            for (int i = 0; i < validTeams.size(); i++) {
                strings[i] = validTeams.get(i).name().toLowerCase();
            }
        }

        return strings;
    }

    /**
     * Adds a member to the smallest {@link GameTeam} if they're not already in a team.
     *
     * @param profile - Profile.
     */
    public static void addMemberIfNotInTeam(@Nonnull PlayerProfile profile) {
        final Player player = profile.getPlayer();
        final Entry entry = Entry.of(player);
        final GameTeam entryTeam = getEntryTeam(entry);

        // Player already has team, OK!
        if (entryTeam != null) {
            return;
        }

        getSmallestTeam().addEntry(entry);
    }
}
