package me.hapyl.fight.game.experience;

import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum ExperienceColor {

    GRAY(ChatColor.GRAY),
    WHITE(ChatColor.WHITE),
    GREEN(ChatColor.GREEN),
    DARK_GREEN(ChatColor.DARK_GREEN),
    AQUA(ChatColor.AQUA),
    BLUE(ChatColor.BLUE),
    YELLOW(ChatColor.YELLOW),
    GOLDEN(ChatColor.GOLD),
    RED(ChatColor.RED),
    DARK_RED(ChatColor.DARK_RED),
    BOLD_DARK_RED(ChatColor.DARK_RED, ChatColor.BOLD);

    private final ChatColor[] colors;
    private final String name;
    private String stringColor;

    ExperienceColor(ChatColor... colors) {
        this.colors = colors;
        this.name = Chat.capitalize(this);
    }

    @Nonnull
    public ChatColor[] getColors() {
        return colors;
    }

    @Nonnull
    public String getColor() {
        if (stringColor == null) {
            final StringBuilder builder = new StringBuilder();
            for (ChatColor color : colors) {
                builder.append(color);
            }

            stringColor = builder.toString();
        }

        return stringColor;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getColor() + name;
    }

    @Nonnull
    public static ExperienceColor getByLevel(long level) {
        return values()[Math.min((int) (level / 5), values().length - 1)];
    }
}
