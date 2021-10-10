package kz.hapyl.fight.game.maps.features;

import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.BlockLocation;
import kz.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MathBooster {

	private final static Map<BlockLocation, MathBooster> boosterMap = new HashMap<>();

	private final GameMaps map;
	private final BlockLocation blockLocation;
	private final Location startLocation;
	private final Location endLocation;
	private final float speed;

	public MathBooster(double x, double y, double z, double x0, double y0, double z0, GameMaps map) {
		this(new BlockLocation((int)x, (int)y, (int)z), new Location(Bukkit.getWorlds().get(0), x0, y0, z0), map);
	}

	public MathBooster(double x, double y, double z, double x0, double y0, double z0) {
		this(new BlockLocation((int)x, (int)y, (int)z), new Location(Bukkit.getWorlds().get(0), x0, y0, z0), GameMaps.CLOUDS);
	}

	public MathBooster(BlockLocation blockLoc, Location end, GameMaps maps) {
		this.blockLocation = blockLoc;
		this.startLocation = new Location(end.getWorld(), blockLoc.getX() + 0.5d, blockLoc.getY() + 0.5d, blockLoc.getZ() + 0.5d);
		this.endLocation = end;
		this.map = maps;
		this.speed = (float)(16.0f / startLocation.distance(end));
		boosterMap.put(blockLoc, this);
	}

	public GameMaps getDesignatedMap() {
		return this.map;
	}

	public BlockLocation getBlockLocation() {
		return blockLocation;
	}

	public ArmorStand launch() {
		final double maxDistance = startLocation.distance(endLocation);
		final Vector vector = endLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(speed);
		final Location location = startLocation.clone();

		final ArmorStand entity = Entities.ARMOR_STAND.spawn(location, me -> {
			me.setMarker(true);
		});

		new GameTask() {
			private double y = 0.0d;
			private double distanceFlown = 0.0d;

			@Override
			public void run() {
				if (distanceFlown >= maxDistance) {
					GameTask.runLater(entity::remove, 10);
					removeRiderFromMap();
					this.cancel();
					return;
				}

				if (distanceFlown > (maxDistance / 2)) {
					y -= speed * 0.4;
				}
				else {
					y += speed * 0.4;
				}

				location.add(0.0d, y, 0.0d);
				entity.teleport(location.add(vector));

				if (rider != null) {
					location.add(0.0d, 0.25d, 0.0d);
					rider.teleport(location);
					location.subtract(0.0d, 0.25d, 0.0d);
				}

				location.subtract(0.0d, y, 0.0d);
				distanceFlown += speed;

			}
		}.addCancelEvent(this::removeRiderFromMap).runTaskTimer(10, 1);

		return entity;
	}

	private Player rider;

	private void removeRiderFromMap() {
		if (rider == null) {
			return;
		}
	}

	public ArmorStand launchAndRide(Player player) {
		final ArmorStand stand = launch();
		rider = player;
		return stand;
	}

	@Nullable
	public static MathBooster byLocation(BlockLocation location) {
		for (final BlockLocation other : boosterMap.keySet()) {
			if (other.compare(location)) {
				return boosterMap.get(other);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "MathBooster{" + "map=" + map +
				", blockLocation=" + blockLocation +
				", startLocation=" + startLocation +
				", endLocation=" + endLocation +
				", speed=" + speed +
				'}';
	}
}
