package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.talents.storage.ArrowShield;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class JuJu extends Hero implements Listener {

	private final Set<Arrow> arrows = new HashSet<>();
	private final double ultimateRadius = 7.5d;

	public JuJu() {
		super("JuJu the Archer");
		this.setInfo("A bandit from the depths of the jungle. Highly skilled in range combat.");
		this.setItem(Material.OAK_SAPLING);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRjZmY0NjU4OGYzOTQ5ODc5NzliN2RkNzcwYWRlYTk0ZDhlZTFmYjFmN2I4NzA0ZTFiYWY5MTIyN2Y2YTRkIn19fQ=="
		);
		equipment.setChestplate(62, 51, 40);
		equipment.setLeggings(62, 51, 40);
		equipment.setBoots(16, 13, 10);

		this.setWeapon(new Weapon(Material.BOW)
				.setName("Twisted")
				.setInfo("A bow made of anything you can find in the middle of the jungle.")
				.setDamage(8.0d));

		this.setUltimate(new UltimateTalent(
				"Kiss of Death, Call of Thunder",
				"Creates a field of arrows at your target block, then, summons thunder to electrolyze everyone who is still in the zone.",
				70
		).setDuration(120).setItem(Material.END_ROD));

	}

	@Override
	public String predicateMessage() {
		return "No valid blocks in sight!";
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getTargetLocation(player) != null;
	}

	private void drawUltimateRadius(Location location) {
		Geometry.drawCircle(location, ultimateRadius, Quality.VERY_HIGH, new WorldParticle(Particle.CRIT));
	}

	private Location getTargetLocation(Player player) {
		final Block targetBlockExact = player.getTargetBlockExact(20);
		return targetBlockExact == null ? null : targetBlockExact.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
	}

	@Override
	public void useUltimate(Player player) {
		final Location location = getTargetLocation(player);
		if (location == null || location.getWorld() == null) {
			return;
		}

		location.add(0.0d, 0.1d, 0.0d);
		setUsingUltimate(player, true);
		new GameTask() {
			private final World world = location.getWorld();
			private double theta = 0.0d;

			@Override
			public void run() {
				drawUltimateRadius(location);
				if (theta >= Math.PI * 2) {
					GameTask.runTaskTimerTimes((task, tick) -> {
						drawUltimateRadius(location);
						final double addX = ThreadLocalRandom.current().nextDouble(ultimateRadius);
						final double addZ = ThreadLocalRandom.current().nextDouble(ultimateRadius);
						final double finalAddX = ThreadRandom.nextBoolean() ? addX : -addX;
						final double finalAddZ = ThreadRandom.nextBoolean() ? addZ : -addZ;

						location.add(finalAddX, 0, finalAddZ);
						world.strikeLightning(location);
						location.subtract(finalAddX, 0, finalAddZ);

						if (tick == 0) {
							setUsingUltimate(player, false);
						}
					}, 3, 40);
					this.cancel();
					return;
				}

				final double x = ultimateRadius * Math.sin(theta);
				final double z = ultimateRadius * Math.cos(theta);

				location.add(x, 5, z);
				world.spawnArrow(location, new Vector(0.0d, -1.0d, 0.0d), 0.75f, 0f);
				location.subtract(x, 5, z);

				theta += Math.PI / 16;

			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public void onStart(Player player) {
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				if (arrows.isEmpty()) {
					return;
				}

				arrows.forEach(arrow -> PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0));
			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public void onStop() {
		arrows.forEach(Entity::remove);
		arrows.clear();
	}

	@EventHandler()
	public void handleProjectileLaunch(ProjectileLaunchEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
			if (validatePlayer(player, Heroes.JUJU) && player.isSneaking() && arrow.isCritical()) {
				arrows.add(arrow);
			}
		}
	}

	@EventHandler()
	public void handleProjectileHit(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && arrows.contains(arrow)) {
			createExplosion(player, arrow.getLocation());
			arrows.remove(arrow);
		}
	}

	private void createExplosion(Player player, Location location) {
		final double y = -1.5d;
		final double spread = 1.55d;
		location.add(0, 2, 0);
		spawnArrow(player, location, new Vector(-spread, y, 0));
		spawnArrow(player, location, new Vector(spread, y, 0));
		spawnArrow(player, location, new Vector(0, y, spread));
		spawnArrow(player, location, new Vector(0, y, -spread));
		spawnArrow(player, location, new Vector(spread, y, spread));
		spawnArrow(player, location, new Vector(spread, y, -spread));
		spawnArrow(player, location, new Vector(-spread, y, spread));
		spawnArrow(player, location, new Vector(-spread, y, -spread));
	}

	private void spawnArrow(Player player, Location location, Vector vector) {
		if (location.getWorld() == null || !location.getBlock().getType().isAir()) {
			return;
		}
		final Arrow arrow = location.getWorld().spawnArrow(location, vector, 1.5f, 0.25f);
		arrow.setDamage(this.getWeapon().getDamage());
		arrow.setShooter(player);
	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		final ArrowShield shield = (ArrowShield)getFirstTalent();
		final Player player = input.getPlayer();
		if (shield.getCharges(player) > 0) {
			shield.removeCharge(player);
			return new DamageOutput(true);
		}
		return null;
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.ARROW_SHIELD.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.CLIMB.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.ELUSIVE_BURST.getTalent();
	}
}
