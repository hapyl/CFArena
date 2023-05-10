package me.hapyl.fight.database.rank;

import me.hapyl.fight.database.PlayerDatabase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum PlayerRank {

    // Default
    DEFAULT(0, RankFormatter.of("&e")),
    PREMIUM(0, RankFormatter.of("&b", "&3")),

    // Administrators
    ADMIN(100, RankFormatter.of("&c🛡 ", "&c", "&f", true)),
    CONSOLE(101, RankFormatter.of("[CONSOLE]")),

    ;

    private final int permissionLevel;
    private final RankFormatter format;

    PlayerRank(int permissionLevel, RankFormatter format) {
        this.permissionLevel = permissionLevel;
        this.format = format;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public String getPrefix() {
        return format.prefix();
    }

    public boolean isAdministrator() {
        return permissionLevel >= 100;
    }

    // static members
    public static PlayerRank getRank(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return CONSOLE;
        }

        else if (sender instanceof Player) {
            return getRank((Player) sender);
        }

        throw new IllegalArgumentException(sender + " cannot have rank");
    }

    public static PlayerRank getRank(Player player) {
        return PlayerDatabase.getDatabase(player).getRank();
    }

    public static boolean hasRank(Player player, PlayerRank rank) {
        return getRank(player) == rank;
    }

}
