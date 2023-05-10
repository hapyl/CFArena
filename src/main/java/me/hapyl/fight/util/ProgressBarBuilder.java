package me.hapyl.fight.util;

import org.bukkit.ChatColor;

public class ProgressBarBuilder {

    private final String indicator;
    private final ChatColor[] colors;
    private int length;

    public ProgressBarBuilder(String indicator, ChatColor color, int length) {
        this.indicator = indicator;
        this.colors = new ChatColor[] { ChatColor.GRAY, color };
        this.length = length;
    }

    public ProgressBarBuilder setPrimaryColor(ChatColor color) {
        colors[0] = color;
        return this;
    }

    public ProgressBarBuilder setSecondaryColor(ChatColor color) {
        colors[1] = color;
        return this;
    }

    public int getLength() {
        return length;
    }

    public ProgressBarBuilder setLength(int length) {
        this.length = length;
        return this;
    }

    public final String build(int value, int max) {
        final StringBuilder builder = new StringBuilder();
        final int percent = (value * length) / max;

        for (int i = 0; i < length; i++) {
            builder.append(percent <= i ? getColor(true) : getColor(false));
            builder.append(indicator);
        }

        return builder.toString();
    }

    private ChatColor getColor(boolean primary) {
        return colors[primary ? 0 : 1];
    }

    public static String of(String indicator, Number value, Number max) {
        return of(indicator, ChatColor.GREEN, value, max);
    }

    public static String of(String indicator, ChatColor color, Number value, Number max) {
        return new ProgressBarBuilder(indicator, color, 20).build(value.intValue(), max.intValue());
    }
}