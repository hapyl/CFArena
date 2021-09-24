package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ShadowShift extends Talent implements Listener {
	public ShadowShift() {
		// Instantly teleports behind target player, but loses ability to move for the teleport time.
		// Instantly teleport behind player you looking at to strike from behind. You will lose ability to move for a short duration.
		super("Shadow Shift", "Instantly teleport behind player you looking at to strike from behind. You will lose ability to move for a short duration.", Type.COMBAT);
		this.setItem(Material.LEAD);
		this.setCd(200);
	}

	@Override
	public Response execute(Player player) {

		final TargetLocation targetLocation = getLocationAndCheck0(player);
		if (targetLocation.getError() != ErrorCode.OK) {
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return Response.error(targetLocation.getError().getErrorMessage());
		}

		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 20));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 20));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 250));

		final Location location = targetLocation.getLocation();
		player.teleport(location);
		PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.0f);
		PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 3, 0.1d, 0.1d, 0.1d, 0.04f);

		return Response.OK;
	}

	private TargetLocation getLocationAndCheck0(Player player) {
		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();
		float radius = 1.25f;

		for (double i = 0; i < 50; i += 0.75) {

			double x = vector.getX() * i;
			double y = vector.getY() * i;
			double z = vector.getZ() * i;
			location.add(x, y, z);

			for (final Player target : Utils.getPlayersInRange(location, radius)) {
				if (target == player || !Manager.current().isPlayerInGame(target)) {
					continue;
				}
				if (!player.hasLineOfSight(target)) {
					final Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1).setY(0.0d));
					behind.setYaw(behind.getYaw());
					behind.setPitch(behind.getPitch());

					if (behind.getBlock().getType().isOccluding()) {
						return new TargetLocation(null, ErrorCode.OCCLUDING);
					}
					else {
						return new TargetLocation(behind, ErrorCode.OK);
					}
				}
				else {
					return new TargetLocation(null, ErrorCode.NO_LOS);
				}
			}
			location.subtract(x, y, z);
		}

		return new TargetLocation(null, ErrorCode.NO_TARGET);
	}

	@EventHandler()
	public void handleEntityLeash(PlayerLeashEntityEvent ev) {
		if (Manager.current().isGameInProgress()) {
			ev.setCancelled(true);
		}
	}

	public static class TargetLocation {

		private final Location location;
		private final ErrorCode error;

		TargetLocation(Location l, ErrorCode r) {
			this.location = l;
			this.error = r;
		}

		public ErrorCode getError() {
			return error;
		}

		public Location getLocation() {
			return location;
		}
	}

	public enum ErrorCode {

		NO_TARGET("No valid target!"),
		NO_LOS("No line of sight with target!"),
		OCCLUDING("Location is not safe!"),
		OK("");

		private final String errorMessage;

		ErrorCode(String s) {
			this.errorMessage = s;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}


}
