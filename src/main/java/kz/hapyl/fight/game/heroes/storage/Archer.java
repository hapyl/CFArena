package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.ItemStacks;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Archer extends Hero implements Listener {

	private final Set<Arrow> boomArrows = new HashSet<>();
	private final Weapon boomBow = new Weapon(Material.BOW).setDamage(0.0d).setName("&6&lBOOM BOW");

	public Archer() {
		super("Archer");
		this.setInfo("One of the best archers joins the fight! Not alone though but with his &bcustom-made &7&obow.");
		this.setItem(Material.BOW);
		this.setWeapon(new Weapon(Material.BOW)
				.setName("Harbinger")
				.setInfo("A custom-made bow with some unique abilities!")
				.setDamage(8.0d));

		final ClassEquipment eq = this.getEquipment();
		eq.setHelmet(Material.CHAINMAIL_HELMET);
		eq.setChestplate(Material.CHAINMAIL_CHESTPLATE);
		eq.setLeggings(Material.LEATHER_LEGGINGS);
		eq.setBoots(Material.LEATHER_BOOTS);

		this.setUltimate(new UltimateTalent(
				"Boom Bow",
				"Equip a &6&lBOOM BOW &7for {duration} that fires explosive arrows which explodes on impact dealing massive damage.",
				50
		).setItem(Material.BLAZE_POWDER).setDuration(120).setCdSec(20).setSound(Sound.ITEM_CROSSBOW_SHOOT, 0.25f));

	}

	@Override
	public void useUltimate(Player player) {
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(4, boomBow.getItem());
		inventory.setHeldItemSlot(4);

		GameTask.runLater(() -> {
			inventory.setItem(4, ItemStacks.AIR);
			inventory.setHeldItemSlot(0);
		}, getUltimateDuration());
	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				boomArrows.forEach(arrow -> {
					if (arrow.isDead()) {
						return;
					}
					PlayerLib.spawnParticle(arrow.getLocation(), Particle.FLAME, 2, 0, 0, 0, 0.015f);
				});
			}
		}.runTaskTimer(0, 2);
	}

	@Override
	public void onStop() {
		Utils.clearCollection(boomArrows);
	}

	@EventHandler()
	public void handleProjectileHitEvent(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow && boomArrows.contains(arrow)) {
			Utils.createExplosion(arrow.getLocation(), 5.0d, 30.0d);
		}
	}

	@EventHandler()
	public void handleProjectileLaunchEvent(ProjectileLaunchEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {

			final int selectedSlot = player.getInventory().getHeldItemSlot();

			// Handle ultimate arrows
			if (isUsingUltimate(player) && selectedSlot == 4) {
				boomArrows.add(arrow);
				return;
			}

			// Handle hawkeye arrows
			if (validatePlayer(player, Heroes.ARCHER) && selectedSlot == 0 && arrow.isCritical() && player.isSneaking()) {
				if (!ThreadRandom.nextFloatAndCheckBetween(0.75f, 1.0f)) {
					return;
				}
				PlayerLib.playSound(player, Sound.ENCHANT_THORNS_HIT, 2.0f);

				new GameTask() {
					@Override
					public void run() {
						if (arrow.isDead()) {
							this.cancel();
							return;
						}

						PlayerLib.spawnParticle(arrow.getLocation(), Particle.CRIT_MAGIC, 1, 0, 0, 0, 0);
						final Entity target = findNearestTarget(player, arrow.getLocation());

						if (target == null) {
							return;
						}

						final Vector vector = target.getLocation()
								.clone()
								.add(0d, 0.5d, 0.0d)
								.toVector()
								.subtract(arrow.getLocation().toVector())
								.normalize()
								.multiply(0.7);
						arrow.setVelocity(vector);


					}
				}.runTaskTimer(0, 1);
			}
		}
	}

	private Entity findNearestTarget(Player shooter, Location location) {
		final World world = location.getWorld();
		if (world == null) {
			return null;
		}

		final Collection<Entity> entities = world.getNearbyEntities(location, 3.0d, 3.0d, 3.0d,
				entity -> entity instanceof Player player ?
						shooter != player && validatePlayer(player) :
						entity instanceof LivingEntity && validateEntity((LivingEntity)entity)
		);

		LivingEntity nearestEntity = null;
		double distance = -1;

		for (final Entity entity : entities) {
			if (!(entity instanceof LivingEntity livingEntity)) {
				continue;
			}

			final double dist = livingEntity.getLocation().distance(location);
			if (nearestEntity == null || dist <= distance) {
				nearestEntity = livingEntity;
				distance = dist;
			}
		}

		return nearestEntity;
	}

	private boolean validateEntity(LivingEntity entity) {
		return !entity.isInvisible() && !entity.isDead() && !(entity instanceof ArmorStand);
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.TRIPLE_SHOT.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SHOCK_DARK.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.HAWKEYE_ARROW.getTalent();
	}

}
