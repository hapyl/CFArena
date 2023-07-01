package me.hapyl.fight.game.color;

import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolator;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nonnull;

public class GradientColor extends Color {

    private final ChatColor to;

    public GradientColor(@Nonnull String hexFrom, @Nonnull String hexTo) {
        this(parseHex(hexFrom), parseHex(hexTo));
    }

    public GradientColor(@Nonnull ChatColor from, @Nonnull ChatColor to) {
        super(from);
        this.to = validateColor(to);
    }

    @Override
    public String color(@Nonnull String string) {
        return color(string, Interpolators.LINEAR);
    }

    public String color(@Nonnull String string, @Nonnull Interpolator interpolator) {
        final Gradient gradient = new Gradient(string);

        for (ColorFlag flag : getFlags()) {
            flag.gradient(gradient);
        }

        return gradient.rgb(color.getColor(), to.getColor(), interpolator);
    }
}