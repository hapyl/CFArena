package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.color.Color;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Month;

public enum Season {
    WINTER("❄", Color.ICY_BLUE, Color.FROSTY_GRAY, Color.ARCTIC_TEAL),
    SPRING("\uD83C\uDF38", Color.SOFT_PINK, Color.PASTEL_GREEN, Color.LIGHT_BLUE),
    SUMMER("☀", Color.GOLDENROD, Color.SUNSHINE_YELLOW, Color.ORANGE),
    FALL("\uD83C\uDF41", Color.FALL_RED, Color.FALL_LIGHT, Color.FALL_ORANGE);

    private static Season currentSeason;

    private final String string;

    Season(String string, Color... colors) {
        this.string = makeString(string, colors);
    }

    @Override
    public String toString() {
        return string;
    }

    private String makeString(String string, Color[] colors) {
        StringBuilder leftBuilder = new StringBuilder();
        StringBuilder rightBuilder = new StringBuilder();

        for (int i = 0; i < colors.length; i++) {
            leftBuilder.append(colors[i]).append(string);
        }

        for (int i = colors.length - 1; i >= 0; i--) {
            rightBuilder.append(colors[i]).append(string);
        }

        final LocalDate today = LocalDate.now();
        final Month month = today.getMonth();
        final int dayOfMonth = today.getDayOfMonth();

        final String dateFormatted = " %s/%s/%s ".formatted(dayOfMonth, month.getValue(), today.getYear());

        return leftBuilder.toString() + ChatColor.DARK_GRAY + dateFormatted + rightBuilder;
    }

    @Nonnull
    public static Season currentSeason() {
        if (currentSeason == null) {
            final LocalDate today = LocalDate.now();
            final Month month = today.getMonth();

            currentSeason = switch (month) {
                case DECEMBER, JANUARY, FEBRUARY -> WINTER;
                case MARCH, APRIL, MAY -> SPRING;
                case JUNE, JULY, AUGUST -> SUMMER;
                case SEPTEMBER, OCTOBER, NOVEMBER -> FALL;
            };
        }

        return currentSeason;
    }

}
