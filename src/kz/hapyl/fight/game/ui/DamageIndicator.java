package kz.hapyl.fight.game.ui;

import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.Random;

public class DamageIndicator {

	private final static int liveTime = 20;

	private final Location location;
	private final double damage;

	public DamageIndicator(Location location, double damage) {
		this.location = location;
		this.damage = damage;

		final ArmorStand stand = Entities.ARMOR_STAND.spawn(randomizeLocation(), me -> {
			me.setInvisible(true);
			me.setInvulnerable(true);
			me.setMarker(true);
			me.setSmall(true);
			me.setCustomName(Chat.format("&b" + BukkitUtils.decimalFormat(damage)));
			me.setCustomNameVisible(true);
		});

		GameTask.runLater(stand::remove, liveTime).runTaskAtCancel();
	}

	private Location randomizeLocation() {
		return location.clone().add(generateRandomDouble(1.5d), generateRandomDouble(0.65d), generateRandomDouble(1.5d));
	}

	private double generateRandomDouble(double range) {
		final double random = new Random().nextDouble() * range;
		return new Random().nextBoolean() ? random : -random;
	}

}
