package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Molotov extends Talent implements Listener {

	public Molotov() {
		super("Hot Hands", "Throw a fireball of fire in front of you. Sets ground on fire upon landing, damaging enemies and healing yourself.", Type.COMBAT);
		this.setItem(Material.FIRE_CHARGE);
		this.setCd(700);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getEyeLocation();
		final Vector vector = location.getDirection().add(new Vector(0.0d, 0.25, 0.0d));

		if (location.getWorld() == null) {
			return Response.error("world is null");
		}

		final Item item = location.getWorld().dropItem(location, new ItemStack(Material.HONEYCOMB));
		item.setPickupDelay(5000);
		item.setTicksLived(5800);
		item.setVelocity(vector.multiply(1.5d));

		new GameTask() {
			private int flightTick = 60;

			@Override
			public void run() {

				// fly down if in air for 3s or more
				if (flightTick-- <= 0) {
					item.setVelocity(new Vector(0.0d, -0.25d, 0.0d));
				}

				// spawn molotov
				if (item.isDead() || item.isOnGround()) {
					item.remove();
					startMolotovTask(item.getLocation(), player);
					this.cancel();
					return;
				}

				// fx
				PlayerLib.spawnParticle(item.getLocation(), Particle.FLAME, 1, 0, 0, 0, 0);

			}
		}.runTaskTimer(0, 1);

		// fx
		PlayerLib.playSound(location, Sound.ENTITY_ARROW_SHOOT, 0.0f);
		return Response.OK;
	}

	private final double molotovRadius = 3.0d;

	private void startMolotovTask(Location location, Player player) {
		new GameTask() {
			private int molotovTime = 20; // 100 / 5

			@Override
			public void run() {
				if (molotovTime-- < 0) {
					this.cancel();
					return;
				}

				Utils.getEntitiesInRange(location, molotovRadius).forEach(entity -> {
					if (entity == player) {
						GamePlayer.getPlayer(player).heal(1.0d);
					}
					else {
						GamePlayer.damageEntity(entity, 3.0d, player, EnumDamageCause.FIRE_MOLOTOV);
					}
				});

				// fx
				PlayerLib.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 2.0f);
				PlayerLib.spawnParticle(location, Particle.FLAME, 15, molotovRadius / 2.0d, 0.1d, molotovRadius / 2.0f, 0.05f);
				Geometry.drawCircle(location, molotovRadius, Quality.HIGH, new WorldParticle(Particle.FLAME));

			}
		}.runTaskTimer(0, 5);
	}

}
