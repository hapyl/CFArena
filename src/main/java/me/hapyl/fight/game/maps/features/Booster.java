package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Booster {

	protected static final Map<BlockLocation, Booster> byLocation = new HashMap<>();

	private final GameMaps designatedMap;
	private final BlockLocation location;
	private final Vector vector;

	public Booster(BlockLocation loc, Vector vec) {
		this(GameMaps.CLOUDS, loc, vec);
	}

	public Booster(GameMaps map, BlockLocation loc, Vector vec) {
		this.designatedMap = map;
		this.location = loc;
		this.vector = vec;
		byLocation.put(loc, this);
	}

	public Booster(int x, int y, int z, double vecX, double vecY, double vecZ) {
		this(new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ));
	}

	public Booster(GameMaps map, int x, int y, int z, double vecX, double vecY, double vecZ) {
		this(map, new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ));
	}

	public GameMaps getDesignatedMap() {
		return designatedMap;
	}

	public Entity launch(boolean debug) {
		final Location location = this.location.centralize();
		final ArmorStand booster = Entities.ARMOR_STAND.spawn(location.add(0.0d, 1.25d, 0.0d), me -> {
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setSmall(true);
			if (debug) {
				me.setCustomName(this.vector.toString());
				me.setCustomNameVisible(true);
			}
			else {
				me.setVisible(false);
			}
		});

		GameTask.runLater(() -> booster.setVelocity(this.vector), 5);
		return booster;
	}

	public Entity launchAndRide(Player player, boolean flag) {
		final Entity piggy = launch(flag);
		piggy.addPassenger(player);

		GamePlayer.getPlayer(player).addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, 200);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
		return piggy;
	}

	public BlockLocation getLocation() {
		return location;
	}

	@Nullable
	public static Booster byLocation(BlockLocation location) {
		for (final BlockLocation other : byLocation.keySet()) {
			if (other.compare(location)) {
				return byLocation.get(other);
			}
		}
		return null;
	}


}
