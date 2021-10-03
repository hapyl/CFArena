package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Vortex extends Hero {

	private final double starDamage = 5.0d;

	public Vortex() {
		super("Vortex");
		this.setInfo("A young boy with power of speaking to starts...");
		this.setItem(Material.NETHER_STAR);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFkYzQ1OGRmYWJjMjBiOGQ1ODdiMDQ3NjI4MGRhMmZiMzI1ZmM2MTZhNTIxMjc4NDQ2NmE3OGI4NWZiN2U0ZCJ9fX0="
		);
		equipment.setChestplate(102, 51, 0);
		equipment.setLeggings(179, 89, 0);
		equipment.setBoots(255, 140, 26);

		this.setWeapon(new Weapon(Material.STONE_SWORD) {

			private final int cooldown = 800;

			@Override
			public void onRightClick(Player player, ItemStack item) {
				if (player.hasCooldown(this.getMaterial())) {
					return;
				}

				final Location location = player.getEyeLocation();

				new GameTask() {
					private final double distanceShift = 0.5d;
					private final double maxDistance = 100;
					private double distanceFlew = 0.0d;

					@Override
					public void run() {

						final Location nextLocation = location.add(player.getEyeLocation().getDirection().multiply(distanceShift));
						PlayerLib.spawnParticle(nextLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);

						if ((distanceFlew % 5) == 0) {
							PlayerLib.playSound(nextLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
						}

						Utils.getEntitiesInRange(nextLocation, 2.0d).forEach(entity -> {
							if (entity == player) {
								return;
							}

							final int damageTicks = entity.getMaximumNoDamageTicks();
							entity.setMaximumNoDamageTicks(0);
							GamePlayer.damageEntity(entity, 0.0d, player, EnumDamageCause.SOTS);
							entity.setMaximumNoDamageTicks(damageTicks);
						});

						if (((distanceFlew += distanceShift) >= maxDistance) || nextLocation.getBlock().getType().isOccluding()) {
							player.setCooldown(Material.STONE_SWORD, cooldown);
							this.cancel();
						}

					}
				}.runTaskTimer(0, 1);

				player.setCooldown(this.getMaterial(), cooldown);
			}
		}.setName("Sword of Thousands Stars")
				.setId("sots_weapon")
				.setLore(
						"A sword with ability to summon thousands stars that follows your crosshair! These stars rapidly hit and knock opponents back."
				)
				.setDamage(8.0d));

		this.setUltimate(new UltimateTalent(
				"All the Stars",
				"Instantly create &b10 &7Astral Stars around you. Then, rapidly slash between them dealing double the damage.__After, perform the final blow with &b360° &7attack that slows opponents.____This will not affect already placed Astral Stars.",
				60
		) {

			private void performFinalSlash(Location location, Player player, double rad) {
				final World world = location.getWorld();
				if (world == null) {
					return;
				}

				for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
					double x = (rad * Math.sin(i));
					double z = (rad * Math.cos(i));
					location.add(x, 0, z);

					// fx
					world.spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
					world.playSound(location, Sound.ITEM_FLINTANDSTEEL_USE, 10, 0.75f);

					// damage
					Utils.getEntitiesInRange(location, 2.0d).forEach(entity -> {
						if (player == entity) {
							return;
						}
						entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
						GamePlayer.damageEntity(entity, 1.0d, player, EnumDamageCause.ENTITY_ATTACK);
					});

					location.subtract(x, 0, z);
				}

			}

			@Override
			public void useUltimate(Player player) {
				final double spreadDistance = 5.5d;
				final double halfSpreadDistance = spreadDistance / 2.0d;
				final Location location = player.getLocation();
				final Location[] allTheStars = {
						//up
						location.clone().add(0, spreadDistance, 0),
						//vert
						location.clone().add(spreadDistance, 0, 0), location.clone().add(-spreadDistance, 0, 0),
						location.clone().add(0, 0, spreadDistance), location.clone().add(0, 0, -spreadDistance),
						//cor
						location.clone().add(halfSpreadDistance, halfSpreadDistance, halfSpreadDistance), location.clone().add(
						-halfSpreadDistance,
						halfSpreadDistance,
						-halfSpreadDistance
				),
						location.clone().add(-halfSpreadDistance, halfSpreadDistance, halfSpreadDistance), location.clone().add(
						halfSpreadDistance,
						halfSpreadDistance,
						-halfSpreadDistance
				),
						//final
						location.clone()
				};

				new GameTask() {
					private int tick = 0;
					private int pos = 0;

					@Override
					public void run() {
						// draw circle
						if (tick % 10 == 0) {
							Geometry.drawCircle(location, spreadDistance, Quality.NORMAL, new WorldParticle(Particle.FIREWORKS_SPARK));
						}
						if (tick++ % 5 == 0) {
							// final slash
							if (pos >= (allTheStars.length - 1)) {
								performFinalSlash(location, player, spreadDistance);
								this.cancel();
								return;
							}
							performStarSlash(allTheStars[pos], allTheStars[pos + 1], player, true);
							++pos;
						}


					}
				}.runTaskTimer(0, 1);
			}
		}.setItem(Material.QUARTZ).setCdSec(30));


	}

	public void performStarSlash(Location start, Location finish, Player player, boolean ultimateStar) {
		// ray-trace path
		Utils.rayTracePath(start, finish, 1.0d, 2.0d, living -> {
			if (living == player) {
				return;
			}
			GamePlayer.damageEntity(living, ultimateStar ? starDamage * 2 : starDamage, player, EnumDamageCause.STAR_SLASH);
		}, loc -> {
			PlayerLib.spawnParticle(loc, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
			PlayerLib.playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f);
		});
	}


	@Override
	public Talent getFirstTalent() {
		return Talents.VORTEX_STAR.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.STAR_ALIGNER.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.EYES_OF_THE_GALAXY.getTalent();
	}
}
