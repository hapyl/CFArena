package me.hapyl.fight.game.color;

import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.gradient.Interpolator;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.util.BFormat;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;

public class GradientColor extends Color {

    private final ChatColor to;

    public GradientColor(@Nonnull String hexFrom, @Nonnull String hexTo) {
        this(parseHex(hexFrom), parseHex(hexTo));
    }

    public GradientColor(@Nonnull ChatColor from, @Nonnull ChatColor to) {
        super(from, org.bukkit.ChatColor.WHITE);
        this.to = validateColor(to);
    }

    @Nonnull
    @Override
    public String color(@Nonnull Object string) {
        return color(string, Interpolators.LINEAR);
    }

    public String color(@Nonnull Object string, @Nonnull Interpolator interpolator) {
        final Gradient gradient = new Gradient(BFormat.format(String.valueOf(string)));

        final ColorFlag[] flags = getFlags();
        if (flags != null) {
            for (ColorFlag flag : flags) {
                flag.gradient(gradient);
            }
        }

        return gradient.rgb(color.getColor(), to.getColor(), interpolator);
    }
}