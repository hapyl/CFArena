package me.hapyl.fight.database.rank;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.SmallCaps;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum PlayerRank {

    // Default
    DEFAULT(0, RankFormatter.of("", Color.YELLOW)),
    VIP(1, RankFormatter.of(Color.VIP + "ᴠɪᴘ", Color.VIP_NAME)),
    PREMIUM(2, RankFormatter.of(Color.PREMIUM + "ᴘʀᴇᴍɪᴜᴍ", Color.PREMIUM_NAME)),

    // Administrators
    MODERATOR(100, RankFormatter.of(Color.MODERATOR.bold() + "ᴍᴏᴅ", Color.DARK_GREEN, Color.WHITE)),
    ADMIN(101, RankFormatter.of(Color.ADMIN.bold() + "ᴀᴅᴍɪɴ", Color.RED, Color.WHITE, true)),
    CONSOLE(102, RankFormatter.of("[CONSOLE]")),

    ;

    private static final int STAFF_LEVEL = 100;
    private static final int ADMIN_LEVEL = 101;

    private final String fallbackName;
    private final int permissionLevel;
    private final RankFormatter format;

    PlayerRank(int permissionLevel, @Nonnull RankFormatter format) {
        this.fallbackName = SmallCaps.format(name());
        this.permissionLevel = permissionLevel;
        this.format = format;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    @Nonnull
    public String getPrefix() {
        return format.prefix();
    }

    @Nonnull
    public String getPrefixWithFallback() {
        final String prefix = format.prefix();

        return !prefix.isEmpty() ? prefix : fallbackName;
    }

    @Nonnull
    public RankFormatter getFormat() {
        return format;
    }

    public boolean isStaff() {
        return permissionLevel >= STAFF_LEVEL;
    }

    public boolean isAdministrator() {
        return permissionLevel >= ADMIN_LEVEL;
    }

    public boolean is(@Nonnull PlayerRank other) {
        return this == other;
    }

    public boolean isOrHigher(@Nonnull PlayerRank other) {
        return this == other || this.permissionLevel >= other.permissionLevel;
    }

    // static members
    public static PlayerRank getRank(@Nonnull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return CONSOLE;
        }

        else if (sender instanceof Player) {
            return getRank((Player) sender);
        }

        throw new IllegalArgumentException(sender + " cannot have rank");
    }

    public static boolean hasOrParent(@Nonnull CommandSender sender, @Nonnull PlayerRank rank) {
        return getRank(sender).isOrHigher(rank);
    }

    @Nonnull
    public static PlayerRank getRank(@Nonnull Player player) {
        return PlayerDatabase.getDatabase(player).getRank();
    }

}
