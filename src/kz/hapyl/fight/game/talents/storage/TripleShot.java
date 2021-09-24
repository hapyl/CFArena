package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TripleShot extends Talent {
	public TripleShot() {
		super("Triple Shot", "Shoots three arrows in front of you. Two additional arrows deal 50% of normal damage.", Type.COMBAT);
		this.setCd(75);
		this.setItem(Material.ARROW);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();

		if (location.getWorld() == null) {
			return Response.error("world is null?");
		}

		// fx
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.25f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.75f);

		final Arrow arrowMiddle = player.launchProjectile(Arrow.class);

		final Arrow arrowLeft = location.getWorld()
				.spawn(player.getLocation()
						.add(player.getLocation().getDirection().add(getVectorLeft(player).multiply(1)))
						.add(0, 1.5, 0), Arrow.class);
		final Arrow arrowRight = location.getWorld()
				.spawn(player.getLocation()
						.add(player.getLocation().getDirection().add(getVectorRight(player).multiply(1)))
						.add(0, 1.5, 0), Arrow.class);

		arrowLeft.setVelocity(arrowMiddle.getVelocity());
		arrowRight.setVelocity(arrowMiddle.getVelocity());

		arrowMiddle.setCritical(true);
		arrowLeft.setCritical(true);
		arrowRight.setCritical(true);

		final double damage = HeroHandle.ALCHEMIST.getWeapon().getDamage();
		arrowMiddle.setDamage(damage);
		arrowLeft.setDamage(damage / 2);
		arrowRight.setDamage(damage / 2);

		arrowMiddle.setShooter(player);
		arrowLeft.setShooter(player);
		arrowRight.setShooter(player);

		return Response.OK;
	}

	private Vector getVectorRight(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
	}

	private Vector getVectorLeft(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
	}

}
