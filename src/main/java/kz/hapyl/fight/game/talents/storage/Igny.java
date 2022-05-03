package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Igny extends Talent {
	public Igny() {
		super("Igni", "Fires blazing spirits in front of you that deal damage to enemies. Damage is scaled with distance.");
		this.setItem(Material.BLAZE_POWDER);
		this.setCdSec(10);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();
		final Location targetLocation = location.add(player.getLocation().getDirection().multiply(3));

		Utils.getPlayersInRange(targetLocation, 4.0d).forEach(target -> {
			if (target == player) {
				return;
			}

			final double distance = targetLocation.distance(target.getLocation());
			double damage = 0.0;

			if (isBetween(distance, 0, 1)) {
				damage = 5.0d;
			}
			else if (isBetween(distance, 1, 2.5)) {
				damage = 3.5d;
			}
			else if (isBetween(distance, 2.5, 4.1d)) {
				damage = 2.0d;
			}

			target.setFireTicks(60);
			GamePlayer.damageEntity(target, damage, player, EnumDamageCause.ENTITY_ATTACK);
		});

		// fx
		PlayerLib.spawnParticle(targetLocation, Particle.FLAME, 20, 2.0, 0.5, 2.0, 0.01f);
		PlayerLib.playSound(targetLocation, Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
		PlayerLib.playSound(targetLocation, Sound.ITEM_FIRECHARGE_USE, 0.0f);

		return Response.OK;
	}

	private boolean isBetween(double a, double min, double max) {
		return a >= min && a < max;
	}

}
