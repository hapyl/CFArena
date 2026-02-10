package me.hapyl.fight.game.color;

import me.hapyl.eterna.module.util.Validate;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * A collection of vanilla and custom colors, supporting hex colors.
 */
@SuppressWarnings("deprecation" /*[target:net.md_5.bungee.api.ChatColor,org.bukkit.ChatColor]*/)
public class Color implements TextColor {
    
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
    
    public static final Color DEFAULT = of("#aabbcc");
    public static final Color SUCCESS = of("#05e30c");
    public static final Color SUCCESS_DARKER = SUCCESS.adjust(0.75f);
    public static final Color ERROR = of("#ed0000");
    public static final Color ERROR_DARKER = ERROR.adjust(0.75f);
    public static final Color BUTTON = of("#F6A623");
    public static final Color SPECTATOR = of("#87B6F5", org.bukkit.ChatColor.GRAY);
    public static final Color MODERATOR = of("#119905", org.bukkit.ChatColor.DARK_GREEN);
    public static final Color ADMIN = of("#CC0826", org.bukkit.ChatColor.RED);
    public static final Color PREMIUM = of("#1E90FF", org.bukkit.ChatColor.AQUA);
    public static final Color PREMIUM_NAME = of("#4169E1", org.bukkit.ChatColor.AQUA);
    public static final Color VIP = of("#00FF7F", org.bukkit.ChatColor.GREEN);
    public static final Color VIP_NAME = of("#00E66B", org.bukkit.ChatColor.GREEN);
    public static final Color DEEP_PURPLE = of("#6A0DAD");
    public static final Color WARM_GRAY = of("#808080");
    public static final Color MINT_GREEN = of("#00FF7F");
    public static final Color DARK_ORANGE = of("#FF8C00");
    public static final Color ROYAL_BLUE = of("#4169E1");
    public static final Color MAROON = of("#800000");
    public static final Color PURPLE_SHADOW = of("#800080");
    public static final Color NAVY_BLUE = of("#001F3F");
    public static final Color MEDIUM_STALE_BLUE = of("#7B68EE");
    public static final Color ICY_BLUE = of("#a6e3e9");
    public static final Color FROSTY_GRAY = of("#d3d3d3");
    public static final Color SILVER = of("#c0c0c0");
    public static final Color ARCTIC_TEAL = of("#00ced1");
    public static final Color WHITE_BLUE_GRAY = of("#a1b5d6");
    public static final Color WITHERS = of("#444477");
    public static final Color DARK_GOLDENROD = of("#B8860B");
    public static final Color FOREST_GREEN = of("#228B22");
    public static final Color STANCE_RANGE = of("#3498db");
    public static final Color STANCE_MELEE = of("#e74c3c");
    public static final Color RIPTIDE = of("#00bcd4");
    public static final Color AMETHYST = of("#9966cc");
    public static final Color EMERALD = of("#50c878");
    public static final Color SAPPHIRE = of("#0F52BA");
    public static final Color ROSE_QUARTZ = of("#FAD4E8");
    public static final Color DIAMOND = of("#B9F2FF");
    public static final Color CORNFLOWER_BLUE = of("#6495ED");
    public static final Color CRIMSON = of("#DC143C");
    public static final Color BLOOD = of("#8B0000");
    public static final Color SPACE = of("#A020F0");
    public static final Color DIDEN = of("#0096aa");
    public static final Color CRAB = of("#b05a5f");
    public static final Color SKY_BLUE = of("#87CEEB");
    public static final Color BUTTON_DARKER = BUTTON.darken(0.8f);
    public static final Color M_YELLOW = of("#FFD700");
    public static final Color GRAYER = GRAY.darken(0.8f);
    public static final Color SKIN = of("#ffdbac");
    public static final Color MOON = of("#b5b08f");
    public static final Color ETHEREAL = of("#D3E1FF");
    public static final Color HEXBANE = of("#5C3D2E");
    public static final Color VOID = of("#3A2A4C");
    public static final Color FALL_DARK = of("#ed6724");
    public static final Color FALL_LIGHT = of("#e89851");
    public static final Color FALL_RED = of("#db4a25");
    public static final Color FALL_ORANGE = of("#f06043");
    public static final Color SUNSHINE_YELLOW = of("#FFD700");
    public static final Color GOLDENROD = of("#DAA520");
    public static final Color ORANGE = of("#FFA500");
    public static final Color SUNSET = of("#f7622a");
    public static final Color SOFT_PINK = of("#FFB6C1");
    public static final Color PASTEL_GREEN = of("#77DD77", org.bukkit.ChatColor.GREEN);
    public static final Color LAVENDER = of("#E6E6FA");
    public static final Color LIGHT_BLUE = of("#ADD8E6");
    public static final Color ECHO_WORLD = of("#363a47");
    public static final Color ABYSS = of("#7304c2");
    public static final Color HELL = of("#8B0000");
    public static final Color EYE = of("#4A9DA5");
    public static final Color DEEP_SKY = of("#00BFFF", org.bukkit.ChatColor.DARK_AQUA);
    public static final Color ITS_WEDNESDAY_MY_DUDES = of("#7BAF35", org.bukkit.ChatColor.DARK_AQUA);
    public static final Color MOSS_GREEN = of("#6e8c07");
    public static final Color BURNT_ORANGE = of("#cf5702");
    public static final Color MINT_CYAN = of("#00ffcc");
    public static final Color CHARCOAL = of("#242424");
    public static final Color EMERALD_GREEN = of("#28b04f");
    public static final Color NEON_GREEN = of("#00e636");
    public static final Color SLATE_GRAY = of("#27373b");
    public static final Color DUSTY_BLUE = of("#808dad");
    public static final Color PEACH = of("#fcad62");
    public static final Color MIDNIGHT_BLUE = of("#0f1026");
    public static final Color PALE_TEAL = of("#9fd6ce");
    public static final Color STEEL_GRAY = of("#6d7070");
    public static final Color LAVENDER_GRAY = of("#a3a1d4");
    public static final Color DARK_CRIMSON = of("#871720");
    public static final Color ICE_BLUE = of("#b3e0ff");
    public static final Color ROYAL_PURPLE = of("#8842ad");
    public static final Color BLOOD_PURPLE = of("#780839");
    public static final Color BLOOD_RED = of("#a30000");
    public static final Color STONE_GRAY = of("#999999");
    public static final Color MUTED_PURPLE = of("#D1B3FF");
    public static final Color SCORCH = of("#FF4500");
    
    
    /**
     * Represents the actual bukkit color.
     */
    public final ChatColor color;
    
    /**
     * Represents the backing {@link org.bukkit.ChatColor}.
     *
     * <p>Actionbar does not support the bukkit {@code §x} format, so we need the backing vanilla color for that because
     * I'm not reworking everything to use components just because I needed the actionbar color once or twice.</p>
     *
     * <p>You can either pass the backing color yourself or let the ChatJIPITI function get the closest vanilla color.</p>
     */
    public final org.bukkit.ChatColor backingColor;
    
    /**
     * Represents the numeric color value, used in {@link TextColor}, encoded following the format:
     * <pre>{@code
     * value = (red << 16) | (green << 8) | blue
     * }</pre>
     */
    public final int value;
    
    Color(@Nonnull ChatColor color, @Nonnull org.bukkit.ChatColor backingColor) {
        Validate.isTrue(backingColor.isColor(), "Backing color must be a color, not formatter!");
        
        this.color = color;
        this.backingColor = backingColor;
        this.value = colorToValue(color);
    }
    
    /**
     * Colors the given object with this color.
     * <p>The invocation of the following method is identical to:</p>
     * <pre>{@code
     *   color + "Hello World!"
     * }</pre>
     *
     * @param string - The object to color.
     * @return the colored object.
     * @see GradientColor#color(Object)
     */
    @Nonnull
    @ApiStatus.Obsolete // Not deprecated because GradientColor requires `color(string)` and cannot simply do `this + string`
    public String color(@Nonnull Object string) {
        return this + string.toString();
    }
    
    /**
     * Gets a brightness-adjusted version of this color by multiplying each RGB component by the given factor.
     * <p>A factor of {@code 1} returns the original color. <p>
     * <p>Values below {@code 1} darken the color, while values above {@code 1} lighten it.</p>
     *
     * @param factor - A value from {@code 0} (black) to {@code 2} (up to double brightness).
     * @return The adjusted {@link Color}.
     * @throws IllegalArgumentException if the factor is outside the {@code 0–2} range.
     */
    @Nonnull
    public final Color adjust(@Range(from = 0, to = 2) float factor) {
        Validate.isTrue(factor >= 0f && factor <= 2f, "factor must be within 0-2 bounds, %s isn't!".formatted(factor));
        
        final java.awt.Color javaColor = color.getColor();
        final int red = (int) Math.clamp(javaColor.getRed() * factor, 0, 255);
        final int green = (int) Math.clamp(javaColor.getGreen() * factor, 0, 255);
        final int blue = (int) Math.clamp(javaColor.getBlue() * factor, 0, 255);
        
        return new Color(
                ChatColor.of(new java.awt.Color(red, green, blue)),
                // We're keeping the backing color because the adjustments are made to
                // brightness, so the actual color remains relatively the same.
                backingColor
        );
    }
    
    /**
     * Gets a darker version of this color based on the given factor.
     * <p>A factor of {@code 0} produces black, while {@code 1} returns the original color.</p>
     *
     * @param factor - The factor by which to darken.
     * @return the darkened color.
     */
    @Nonnull
    public final Color darken(@Range(from = 0, to = 1) float factor) {
        return adjust(factor);
    }
    
    /**
     * Gets a lighter version of this color based on the given factor.
     * <p>A factor of {@code 0} returns the original color, while {@code 1} doubles its brightness.</p>
     *
     * @param factor - The factor by which to darken.
     * @return the darkened color.
     */
    @Nonnull
    public final Color lighten(@Range(from = 0, to = 1) float factor) {
        return adjust(1 + factor);
    }
    
    /**
     * Gets a string representation of this color, following the bukkit format:
     * <pre>{@code
     * string = "§" + char
     * }</pre>
     *
     * @return a string representation of this color.
     */
    @Override
    public final String toString() {
        return color.toString();
    }
    
    /**
     * Gets the color appended by the bold character, resulting in bolded color.
     *
     * @return the color appended by the bold character, resulting in bolded color.
     */
    @Nonnull
    public String bold() {
        return color + ChatColor.BOLD.toString();
    }
    
    /**
     * Gets the color appended by the underline character, resulting in underlined color.
     *
     * @return the color appended by the underline character, resulting in underlined color.
     */
    @Nonnull
    public String underlined() {
        return color + ChatColor.UNDERLINE.toString();
    }
    
    /**
     * Wraps the given string in bold formatting, then resets formatting back to this color.
     * <p>Equivalent to:</p>
     * <pre>{@code
     * string = color.bold() + string + color
     * }</pre>
     *
     * @param string - The text to format in bold. May be {@code null}.
     * @return The formatted string with bold applied and color reset.
     */
    @Nonnull
    public String boldThenReset(@Nullable Object string) {
        return this.bold() + string + this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int value() {
        return value;
    }
    
    /**
     * Parses the given string as hex color into {@link ChatColor}.
     * <p>The implementation differs from {@link ChatColor#of(String)} because it doesn't throw exception and instead returns a {@link ChatColor#BLACK} color if parsing failed.</p>
     *
     * @param hex - The hex to parse; May or may not start with {@code #}, but must be exactly of {@code 6} length (Excluding the #).
     * @return the parsed {@link ChatColor}.
     */
    @Nonnull
    public static ChatColor parse(@Nonnull String hex) {
        String actualHex = hex.startsWith("#") ? hex : "#" + hex;
        
        final int length = actualHex.length();
        
        // If hex > 7, substring it
        if (length > 7) {
            actualHex = actualHex.substring(0, 7);
        }
        // Otherwise, if the hex is less than 7, append 'A' * 7 - length
        else if (length < 7) {
            actualHex = actualHex + "A".repeat(7 - length);
        }
        
        try {
            return ChatColor.of(actualHex);
        }
        catch (Exception e) {
            return ChatColor.BLACK;
        }
    }
    
    @Nonnull
    public static Color of(@Nonnull String hex) {
        final ChatColor color = parse(hex);
        
        // Find the closest bukkit color based on hex
        org.bukkit.ChatColor backingColor = null;
        double closest = Double.MAX_VALUE;
        
        for (org.bukkit.ChatColor chatColor : org.bukkit.ChatColor.values()) {
            if (!chatColor.isColor()) {
                continue;
            }
            
            int red = color.getColor().getRed() - chatColor.asBungee().getColor().getRed();
            int green = color.getColor().getGreen() - chatColor.asBungee().getColor().getGreen();
            int blue = color.getColor().getBlue() - chatColor.asBungee().getColor().getBlue();
            
            double distance = Math.sqrt(red * red + green * green + blue * blue);
            
            if (distance < closest) {
                backingColor = chatColor;
                closest = distance;
            }
        }
        
        return new Color(color, backingColor != null ? backingColor : org.bukkit.ChatColor.BLACK);
    }
    
    @Nonnull
    public static Color of(@Nonnull String hex, @Nonnull org.bukkit.ChatColor backingColor) {
        return new Color(parse(hex), backingColor);
    }
    
    @Nonnull
    private static Color bukkit(@Nonnull ChatColor color) {
        final java.awt.Color javaColor = color.getColor();
        final org.bukkit.ChatColor backingColor
                = Arrays.stream(org.bukkit.ChatColor.values())
                        .filter(chatColor -> chatColor.isColor() && chatColor.asBungee() == color)
                        .findFirst()
                        .orElse(org.bukkit.ChatColor.BLACK);
        
        return new Color(ChatColor.of(new java.awt.Color(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue())), backingColor);
    }
    
    private static int colorToValue(@Nonnull ChatColor color) {
        final java.awt.Color javaColor = color.getColor();
        
        return (javaColor.getRed() << 16) | (javaColor.getGreen() << 8) | javaColor.getBlue();
    }
    
}