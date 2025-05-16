package me.hapyl.fight.game.color;

import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.gradient.Interpolator;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.util.BFormat;

import javax.annotation.Nonnull;

public class GradientColor extends Color {

    private final Color to;
    
    public GradientColor(@Nonnull String hexFrom, @Nonnull String hexTo) {
        super(parse(hexFrom), org.bukkit.ChatColor.WHITE);
        
        this.to = of(hexTo);
    }

    @Nonnull
    @Override
    public String color(@Nonnull Object string) {
        return color(string, Interpolators.LINEAR);
    }

    public void flags(@Nonnull Gradient gradient) {
        gradient.makeBold();
    }
    
    @Nonnull
    public String color(@Nonnull Object string, @Nonnull Interpolator interpolator) {
        final Gradient gradient = new Gradient(BFormat.format(String.valueOf(string)));
        flags(gradient);
        
        return gradient.rgb(color.getColor(), to.color.getColor(), interpolator);
    }
}