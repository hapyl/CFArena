package me.hapyl.fight.game.ui;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Random;

public class DamageIndicator {

	private final Hologram hologram;
	private final Location location;

	public DamageIndicator(Location location, double damage) {
		this.location = location;

		hologram = new Hologram();
		hologram.addLine("&a&l" + BukkitUtils.decimalFormat(damage));

		//		if (points > 0) {
		//			hologram.addLine("&b+%s &l※".formatted(points));
		//		}

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
