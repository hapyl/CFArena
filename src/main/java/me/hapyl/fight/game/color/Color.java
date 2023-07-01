package me.hapyl.fight.game.color;

import me.hapyl.fight.util.Range;
import me.hapyl.spigotutils.module.math.Numbers;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Color {

    public static final Color DEFAULT = new Color("#aabbcc");
    public static final Color SUCCESS = new Color("#05e30c");
    public static final Color SUCCESS_DARKER = SUCCESS.adjust(0.75f);
    public static final Color ERROR = new Color("#ed0000");
    public static final Color ERROR_DARKER = ERROR.adjust(0.75f);

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

    public String color(@Nonnull String string) {
        return this + string;
    }

    public final Color adjust(@Range(max = 2) float factor) {
        factor = Numbers.clamp(factor, 0, 2);

        final java.awt.Color javaColor = color.getColor();

        return new Color(
                (int) (javaColor.getRed() * factor),
                (int) (javaColor.getGreen() * factor),
                (int) (javaColor.getBlue() * factor)
        );
    }

    public ColorFlag[] getFlags() {
        return flags;
    }

    public Color setFlags(@Nullable ColorFlag... flags) {
        this.flags = flags;
        return this;
    }

    public final Color darken(@Range(max = 1) float factor01) {
        return adjust(factor01);
    }

    public final Color lighten(@Range(min = 1, max = 2) float factor12) {
        return adjust(factor12);
    }

    @Override
    public final String toString() {
        return formatFlags() + color.toString();
    }

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
}