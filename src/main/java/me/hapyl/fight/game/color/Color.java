package me.hapyl.fight.game.color;

import me.hapyl.fight.util.Range;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BFormat;
import me.hapyl.spigotutils.module.util.Validate;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A custom color formatting.
 * Allows creating and using #hex colors.
 */
public class Color {

    // *=* Bukkit Colors *=*

    public static final Color BLACK = bukkit(ChatColor.BLACK);
    public static final Color DARK_BLUE = bukkit(ChatColor.DARK_BLUE);
    public static final Color DARK_GREEN = bukkit(ChatColor.DARK_GREEN);
    public static final Color DARK_AQUA = bukkit(ChatColor.DARK_AQUA);
    public static final Color DARK_RED = bukkit(ChatColor.DARK_RED);
    public static final Color DARK_PURPLE = bukkit(ChatColor.DARK_PURPLE);
    public static final Color GOLD = bukkit(ChatColor.GOLD);
    public static final Color GRAY = bukkit(ChatColor.GRAY);
    public static final Color DARK_GRAY = bukkit(ChatColor.DARK_GRAY);
    public static final Color BLUE = bukkit(ChatColor.BLUE);
    public static final Color GREEN = bukkit(ChatColor.GREEN);
    public static final Color AQUA = bukkit(ChatColor.AQUA);
    public static final Color RED = bukkit(ChatColor.RED);
    public static final Color LIGHT_PURPLE = bukkit(ChatColor.LIGHT_PURPLE);
    public static final Color YELLOW = bukkit(ChatColor.YELLOW);
    public static final Color WHITE = bukkit(ChatColor.WHITE);

    // *=* Custom Colors *=*

    public static final Color DEFAULT = new Color("#aabbcc");
    public static final Color SUCCESS = new Color("#05e30c");
    public static final Color SUCCESS_DARKER = SUCCESS.adjust(0.75f);
    public static final Color ERROR = new Color("#ed0000");
    public static final Color ERROR_DARKER = ERROR.adjust(0.75f);
    public static final Color BUTTON = new Color("#F6A623");
    public static final Color SPECTATOR = new Color("#87B6F5", org.bukkit.ChatColor.GRAY);
    public static final Color MODERATOR = new Color("#119905", org.bukkit.ChatColor.DARK_GREEN);
    public static final Color ADMIN = new Color("#CC0826", org.bukkit.ChatColor.RED);
    public static final Color PREMIUM = new Color("#1E90FF", org.bukkit.ChatColor.AQUA);
    public static final Color PREMIUM_NAME = new Color("#4169E1", org.bukkit.ChatColor.AQUA);
    public static final Color VIP = new Color("#00FF7F", org.bukkit.ChatColor.GREEN);
    public static final Color VIP_NAME = new Color("#00E66B", org.bukkit.ChatColor.GREEN);
    public static final Color DEEP_PURPLE = new Color("#6A0DAD");
    public static final Color WARM_GRAY = new Color("#808080");
    public static final Color MINT_GREEN = new Color("#00FF7F");
    public static final Color DARK_ORANGE = new Color("#FF8C00");
    public static final Color ROYAL_BLUE = new Color("#4169E1");
    public static final Color MAROON = new Color("#800000");
    public static final Color PURPLE_SHADOW = new Color("#800080");
    public static final Color NAVY_BLUE = new Color("#001F3F");
    public static final Color MEDIUM_STALE_BLUE = new Color("#7B68EE");
    public static final Color ICY_BLUE = new Color("#a6e3e9");
    public static final Color FROSTY_GRAY = new Color("#d3d3d3");
    public static final Color SILVER = new Color("#c0c0c0");
    public static final Color ARCTIC_TEAL = new Color("#00ced1");
    public static final Color WHITE_BLUE_GRAY = new Color("#a1b5d6").setFlags(ColorFlag.BOLD);
    public static final Color WITHERS = new Color("#444477");
    public static final Color DARK_GOLDENROD = new Color("#B8860B");
    public static final Color FOREST_GREEN = new Color("#228B22");
    public static final Color STANCE_RANGE = new Color("#3498db");
    public static final Color STANCE_MELEE = new Color("#e74c3c");
    public static final Color RIPTIDE = new Color("#00bcd4");
    public static final Color AMETHYST = new Color("#9966cc");
    public static final Color EMERALD = new Color("#50c878");
    public static final Color SAPPHIRE = new Color("#0F52BA");
    public static final Color ROSE_QUARTZ = new Color("#FAD4E8");
    public static final Color DIAMOND = new Color("#B9F2FF");
    public static final Color CORNFLOWER_BLUE = new Color("#6495ED");
    public static final Color CRIMSON = new Color("#DC143C");
    public static final Color BLOOD = new Color("#8B0000");
    public static final Color SPACE = new Color("#A020F0");
    public static final Color DIDEN = new Color("#0096aa");
    public static final Color CRAB = new Color("#b05a5f");
    public static final Color SKY_BLUE = new Color("#87CEEB");
    public static final Color BUTTON_DARKER = BUTTON.darken(0.8f);

    // *-----------------------------------------------------*

    public final ChatColor color;
    // Backing 'bukkitChatColor' is needed for teams;
    // since they cannot have custom colors for
    // some reason, maybe mojang just forgot about it,
    // just like they did with the actionbar ¯\_(ツ)_/¯
    /// iTs BeCaUsE oF gLoWiNg!!!
    /// First, it's just stupid that glowing is based on a team color rather than LITERALLY ANYTHING ELSE.
    /// And second, JUST MAKE GLOWING USE ANY COLOR WE'RE NOT IN A FUCKING 1995.
    /// - hapyl, signing off
    public final org.bukkit.ChatColor bukkitChatColor;
    public final org.bukkit.Color bukkitColor;
    private ColorFlag[] flags;

    public Color(int red, int green, int blue) {
        this(red, green, blue, org.bukkit.ChatColor.WHITE);
    }

    public Color(int red, int green, int blue, org.bukkit.ChatColor backingColor) {
        this(validateColor(ChatColor.of(new java.awt.Color(
                        Numbers.clamp(red, 0, 255),
                        Numbers.clamp(green, 0, 255),
                        Numbers.clamp(blue, 0, 255)
                )
        )), backingColor);
    }

    public Color(@Nonnull String hex) {
        this(hex, org.bukkit.ChatColor.WHITE);
    }

    public Color(@Nonnull String hex, org.bukkit.ChatColor backingColor) {
        this(parseHex(hex), backingColor);
    }

    public Color(@Nonnull org.bukkit.Color color) {
        this(parseHex("%02X%02X%02X".formatted(color.getRed(), color.getGreen(), color.getBlue())), org.bukkit.ChatColor.WHITE);
    }

    @Super
    public Color(@Nonnull ChatColor color, @Nonnull org.bukkit.ChatColor backingColor) {
        Validate.isTrue(backingColor.isColor(), "Backing color must be a color, not formatter!");

        this.color = validateColor(color);
        this.bukkitChatColor = backingColor;

        final java.awt.Color javaColor = color.getColor();
        this.bukkitColor = org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
    }

    private Color(Color color) {
        this.color = color.color;
        this.flags = color.flags;
        this.bukkitColor = color.bukkitColor;
        this.bukkitChatColor = color.bukkitChatColor;
    }

    /**
     * Colors the string and formats with a {@link BFormat}.
     *
     * @param string - String to color.
     * @param format - Format, if needed.
     * @return the formatted string.
     */
    @Nonnull
    public String color(@Nonnull Object string, @Nullable Object... format) {
        return this + BFormat.format(String.valueOf(string), format);
    }

    /**
     * Adjusts the color by the factor.
     *
     * @param factor - Factor by which to adjust the color.
     *               <b>The factor must be between 0 and 2. Where 0 = decrease by 100%, where 2 = increase by 100%.</b>
     * @return The adjusted color.
     */
    public final Color adjust(@Range(max = 2) float factor) {
        factor = Numbers.clamp(factor, 0, 2);

        final java.awt.Color javaColor = color.getColor();

        return new Color(
                (int) (javaColor.getRed() * factor),
                (int) (javaColor.getGreen() * factor),
                (int) (javaColor.getBlue() * factor)
        );
    }

    /**
     * Gets the flags for this color.
     *
     * @return the color flags.
     * @see ColorFlag
     */
    @Nullable
    public ColorFlag[] getFlags() {
        return flags;
    }

    /**
     * Sets the color flags.
     *
     * @param flags - Color flags.
     * @see ColorFlag
     */
    public Color setFlags(@Nullable ColorFlag... flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Darkens the color by the given factor.
     *
     * @param factor01 - Factor by which to adjust the color.
     *                 <b>The factor must be between 0 and 1.</b>
     */
    public final Color darken(@Range(max = 1) float factor01) {
        return adjust(factor01);
    }

    /**
     * Lightens the color by the given factor.
     *
     * @param factor12 - Factor by which to adjust the color.
     *                 <b>The factor must be between 1 and 2.</b>
     */
    public final Color lighten(@Range(min = 1, max = 2) float factor12) {
        return adjust(factor12);
    }

    /**
     * Gets the string representation of this color, with flags if there are any.
     *
     * @return the string.
     */
    @Override
    public final String toString() {
        return formatFlags() + color.toString();
    }

    /**
     * Gets the flags for this color in string representation, or empty string if there are no flags.
     *
     * @return the string.
     */
    @Nonnull
    public String formatFlags() {
        if (this.flags == null) {
            return "";
        }

        final StringBuilder prefix = new StringBuilder();

        for (ColorFlag flag : this.flags) {
            prefix.append(flag.color);
        }

        return prefix.toString();
    }

    /**
     * Returns true if this color has a given flag; false otherwise.
     *
     * @param flag - Flag to check.
     * @return true if the color has a given flag; false otherwise.
     */
    public boolean hasFlag(@Nullable ColorFlag flag) {
        if (this.flags == null || flag == null) {
            return false;
        }

        for (ColorFlag colorFlag : this.flags) {
            if (colorFlag == flag) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the bold version of this color in string.
     * This does not change the color.
     *
     * @return the string.
     */
    @Nonnull
    public String bold() {
        return color + ChatColor.BOLD.toString();
    }

    /**
     * Gets the bukkit color.
     *
     * @return the bukkit color.
     */
    @Nonnull
    public org.bukkit.Color getBukkitColor() {
        return bukkitColor;
    }

    @Nonnull
    public org.bukkit.ChatColor getBukkitChatColor() {
        return bukkitChatColor;
    }

    @Nonnull
    public String boldAndDefault(String string) {
        return this.bold() + string + this;
    }

    /**
     * Parses a {@link ChatColor} from hex.
     *
     * @param hex - Hex to parse.
     *            <b>May, or may not include the "#" character.</b>
     * @return the {@link ChatColor}.
     */
    public static ChatColor parseHex(@Nonnull String hex) {
        if (hex.startsWith("#") && hex.length() == 7) {
            return ChatColor.of(hex);
        }
        else {
            hex = "#" + hex.substring(0, Math.min(hex.length(), 7));

            if (hex.length() < 7) {
                hex = hex + "a".repeat(7 - hex.length());
            }

            return ChatColor.of(hex.substring(0, Math.min(hex.length(), 7)));
        }
    }

    protected static ChatColor validateColor(@Nullable ChatColor color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null");
        }

        if (color.getColor() == null) {
            throw new IllegalArgumentException("formatting cannot be used as color: " + color);
        }

        return color;
    }

    @Nonnull
    private static Color bukkit(ChatColor chatColor) {
        final java.awt.Color javaColor = chatColor.getColor();
        return new Color(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), bungeeToBukkitColor(chatColor));
    }

    @Nonnull
    private static org.bukkit.ChatColor bungeeToBukkitColor(ChatColor color) {
        for (org.bukkit.ChatColor chatColor : org.bukkit.ChatColor.values()) {
            if (chatColor.asBungee() == color) {
                return chatColor;
            }
        }

        return org.bukkit.ChatColor.WHITE;
    }
}