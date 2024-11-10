package me.hapyl.fight.database.rank;

import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum PlayerRank {

    DEFAULT(0, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return "";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.YELLOW;
        }

    }),
    VIP(1, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return Color.VIP.bold() + "ᴠɪᴘ";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.VIP_NAME;
        }

        @Nonnull
        @Override
        public String joinMessage() {
            return "{player} &6is here!";
        }

        @Nonnull
        @Override
        public String leaveMessage() {
            return "{player} &6has left!";
        }
    }),

    PREMIUM(2, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return Color.PREMIUM.bold() + "ᴘʀᴇᴍɪᴜᴍ";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.PREMIUM_NAME;
        }

        @Override
        public String joinMessage() {
            return "{player} &6has arrived!";
        }

        @Override
        public String leaveMessage() {
            return "{player} &6has departed!";
        }
    }),

    // Game manager are NOT administrators
    GAME_MANAGER(99, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return Color.FOREST_GREEN.bold() + "ɢᴍ";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.PASTEL_GREEN;
        }

        @Nullable
        @Override
        public String joinMessage() {
            return PlayerRank.PREMIUM.format.joinMessage();
        }

        @Nullable
        @Override
        public String leaveMessage() {
            return PlayerRank.PREMIUM.format.leaveMessage();
        }
    }),

    // Administrators
    MODERATOR(100, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return Color.MODERATOR.bold() + "ᴍᴏᴅ";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.DARK_GREEN;
        }

        @Nullable
        @Override
        public String joinMessage() {
            return null;
        }

        @Nullable
        @Override
        public String leaveMessage() {
            return null;
        }
    }),

    ADMIN(101, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return Color.ADMIN.bold() + "ᴀᴅᴍɪɴ";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.RED;
        }

        @Override
        public boolean allowFormatting() {
            return true;
        }

        @Nullable
        @Override
        public String joinMessage() {
            return null;
        }

        @Nullable
        @Override
        public String leaveMessage() {
            return null;
        }
    }),

    CONSOLE(102, new RankFormatter() {
        @Nonnull
        @Override
        public String prefix() {
            return "[Console]";
        }

        @Nonnull
        @Override
        public Color nameColor() {
            return Color.DARK_RED;
        }
    }),

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

    public int reverseOrdinal() {
        return values().length - ordinal();
    }

    public boolean isOrHigher(@Nonnull CommandSender player) {
        return getRank(player).isOrHigher(this);
    }

    public boolean isOrHigher(@Nonnull GamePlayer player) {
        return isOrHigher(player.getPlayer());
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
        return CF.getDatabase(player).getRank();
    }

}
