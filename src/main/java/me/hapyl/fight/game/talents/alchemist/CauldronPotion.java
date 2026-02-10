package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.particle.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class CauldronPotion implements AlchemistEffect {

    private final Color[] colors;
    private final ParticleBuilder builder;

    public CauldronPotion(@Nonnull Color from, @Nonnull Color to) {
        this.colors = new Color[] { from, to };
        this.builder = ParticleBuilder.dustTransition(from, to, 1);
    }

    @Nonnull
    @Override
    public String name() {
        return "";
    }

    @Nonnull
    @Override
    public Color color() {
        return colors[0];
    }

    public void onTick(@Nonnull Location location) {
        builder.display(location, 1);
    }

}
