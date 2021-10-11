package kz.hapyl.fight.game.ui;

import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.hologram.Hologram;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;

import java.util.Random;

public class DamageIndicator {

	private final Location location;

	public DamageIndicator(Location location, double damage, int points) {
		this.location = location;

		final Hologram hologram = new Hologram();
		hologram.addLine("&a&l" + BukkitUtils.decimalFormat(damage));
		if (points > 0) {
			hologram.addLine("&b+%s &l※".formatted(points));
		}

		hologram.create(randomizeLocation());
		hologram.showAll();

		GameTask.runLater(hologram::hide, 20).runTaskAtCancel();
	}

	private Location randomizeLocation() {
		return location.clone().add(generateRandomDouble(), new Random().nextDouble() * 0.25d, generateRandomDouble());
	}

	private double generateRandomDouble() {
		final double random = new Random().nextDouble() * 1.5d;
		return new Random().nextBoolean() ? random : -random;
	}

}
