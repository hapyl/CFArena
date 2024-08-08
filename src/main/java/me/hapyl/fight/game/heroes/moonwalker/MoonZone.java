package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.Ticking;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class MoonZone implements Ticking, Removable {

    protected final GamePlayer player;
    protected final Location centre;
    protected final double size;

    protected final Hologram hologram;

    protected double energy;

    public MoonZone(GamePlayer player, Location centre, double size, int energy) {
        this.player = player;
        this.centre = centre;
        this.size = size;
        this.energy = energy;

        this.hologram = new Hologram();

        LocationHelper.modify(centre, 0, hologramOffset(), 0, location -> {
            this.hologram.create(location);
        });

        // Show to self (Maybe show to teammates as well?)
        this.hologram.show(player.getPlayer());
    }

    public double decreaseEnergy(double amount, double scale) {
        final double decreased = Math.min(amount, energy);
        energy -= decreased;

        return decreased * scale;
    }

    @Override
    public boolean shouldRemove() {
        return energy <= 0;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        if (energy-- <= 0) {
            return;
        }

        // Update hologram
        hologram.setLinesAndUpdate(
                "&e&lMOON ENERGY",
                "&b%s".formatted(energy)
        );

        // Tick ground
        Geometry.drawCircle(centre, size, Quality.VERY_HIGH, new WorldParticle(Particle.CRIT));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void remove() {
        hologram.destroy();
    }

    protected double hologramOffset() {
        return 3;
    }

}
