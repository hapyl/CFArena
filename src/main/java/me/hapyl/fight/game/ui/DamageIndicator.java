package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Random;

/**
 * A damage indicator.
 */
@Deprecated
public class DamageIndicator {

    private final Hologram hologram;
    private final Location location;

    public DamageIndicator(Location location, double damage, boolean isCrit) {
        this.location = location;

        final String damageString = BukkitUtils.decimalFormat(damage);

        hologram = new Hologram();

        if (isCrit) {
            hologram.addLine("&e&l" + damageString + "&c&l✷");
        }
        else {
            hologram.addLine("&a&l" + damageString);
        }
    }

    public void setExtra(Collection<String> extra) {
        for (final String str : extra) {
            hologram.addLine(str);
        }
    }

    public void display(int duration) {
        hologram.create(randomizeLocation());
        hologram.showAll();

        GameTask.runLater(hologram::destroy, duration).runTaskAtCancel();
    }

    private Location randomizeLocation() {
        return location.clone().add(generateRandomDouble(), new Random().nextDouble() * 0.25d, generateRandomDouble());
    }

    private double generateRandomDouble() {
        final double random = new Random().nextDouble() * 1.5d;
        return new Random().nextBoolean() ? random : -random;
    }

}
