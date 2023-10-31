package me.hapyl.fight.game.color;

import me.hapyl.fight.util.Range;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BFormat;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A custom color formatting.
 * Allows creating and using #hex colors.
 */
public class Color {

    // Bukkit colors start

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

    // Bukkit colors end

    public static final Color DEFAULT = new Color("#aabbcc");

    public static final Color SUCCESS = new Color("#05e30c");
    public static final Color SUCCESS_DARKER = SUCCESS.adjust(0.75f);

    public static final Color ERROR = new Color("#ed0000");
    public static final Color ERROR_DARKER = ERROR.adjust(0.75f);

    public static final Color BUTTON = new Color("#F6A623");

    public static final Color SPECTATOR = new Color("#87B6F5");
    public static final Color MODERATOR = new Color("#119905");
    public static final Color ADMIN = new Color("#CC0826");

    public static final Color DEEP_PURPLE = new Color("#6A0DAD");

    public static final Color WARM_GRAY = new Color("#808080");
    public static final Color MINT_GREEN = new Color("#00FF7F");
    public static final Color DARK_ORANGE = new Color("#FF8C00");
    public static final Color ROYAL_BLUE = new Color("#4169E1");
    public static final Color MAROON = new Color("#800000");

    public final ChatColor color;
    private ColorFlag[] flags;

    public Color(int red, int green, int blue) {
        this.color = validateColor(ChatColor.of(new java.awt.Color(
                        Numbers.clamp(red, 0, 255),
                        Numbers.clamp(green, 0, 255),
                        Numbers.clamp(blue, 0, 255)
                )
        ));
    }

    public Color(@Nonnull String hex) {
        this(parseHex(hex));
    }

    public Color(@Nonnull ChatColor color) {
        this.color = validateColor(color);
    }

    /**
     * Colors the string and formats with a {@link BFormat}.
     *
     * @param string - String to color.
     * @param format - Format, if needed.
     * @return the formatted string.
     */
    @Nonnull
    public String color(@Nonnull String string, @Nullable Object... format) {
        return this + BFormat.format(string, format);
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
    public org.bukkit.Color toBukkitColor() {
        final java.awt.Color javaColor = color.getColor();
        return org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
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

        return new Color(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
    }
}