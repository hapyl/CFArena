package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.IGamePlayer;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class Pytaria extends Hero {

	private final int healthRegenPercent = 20;

	public Pytaria() {
		super("Pytaria");
		this.setItem(Material.POPPY);
		this.setInfo("Beautiful, but deadly opponent with addiction to flowers. She suffered all her youth, which at the end, made her only stronger.");

		this.setWeapon(new Weapon(Material.ALLIUM).setName("Annihilallium").setDamage(5.0).setLore("A beautiful flower, nothing more."));

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JiMDc1MmY5ZmE4N2E2OTNjMmQwZDlmMjk1NDkzNzVmZWI2Zjc2OTUyZGE5MGQ2ODgyMGU3OTAwMDgzZjgwMSJ9fX0=");
		equipment.setChestplate(255, 128, 128);
		equipment.setLeggings(51, 102, 255);
		equipment.setBoots(179, 204, 204);

		// Summons a blooming bee in front of her that locks to a closest enemy and deals damage (if then don't have any cover) that depends on how low her health is and regenerates &b" + healthRegenPercent + "% &7of missing health.

		this.setUltimate(new UltimateTalent("Feel the Breeze", "Summon a blooming Bee in front of Pytaria.____The Bee will lock on a closest enemy and charge.____Once charged, unleashes damage in small AoE and regenerates &b" + healthRegenPercent + "% &7of Pytaria's missing health.", 60) {
			@Override
			public void useUltimate(Player player) {
				final IGamePlayer gp = GamePlayer.getPlayerSafe(player);
				final double health = gp.getHealth();
				final double maxHealth = gp.getMaxHealth();
				final double missingHp = (maxHealth - health) * healthRegenPercent / maxHealth;

				final double finalDamage = calculateDamage(player, 10.0d);

				final Location location = player.getLocation();
				final Vector vector = location.getDirection();
				location.add(vector.setY(0).multiply(5));
				location.add(0, 7, 0);

				final Bee bee = Entities.BEE.spawn(location, me -> {
					me.setSilent(true);
					me.setAI(false);
				});

				final Player nearestPlayer = Utils.getNearestPlayer(location, 50, player);
				PlayerLib.playSound(location, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f);

				new GameTask() {
					private int windupTime = 60;

					@Override
					public void run() {

						final Location lockLocation = nearestPlayer == null ? location.clone().subtract(0, 9, 0) : nearestPlayer.getLocation();
						final Location touchLocation = drawLine(location.clone(), lockLocation.clone());

						// BOOM
						if (windupTime-- <= 0) {
							PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);
							PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 5, 0.2, 0.2, 0.2, 0.1f);
							PlayerLib.playSound(location, Sound.ENTITY_BEE_DEATH, 1.5f);
							bee.remove();
							this.cancel();

							Utils.getPlayersInRange(touchLocation, 1.5d).forEach(victim -> {
								victim.damage(finalDamage, player);
							});

							PlayerLib.spawnParticle(touchLocation, Particle.EXPLOSION_LARGE, 3, 0.5, 0, 0.5, 0);
							PlayerLib.playSound(touchLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.25f);
						}

					}
				}.runTaskTimer(0, 1);

				Chat.sendMessage(player, "&a&l][ &a%s healed for &c%s❤ &a!", this.getName(), BukkitUtils.decimalFormat(missingHp));
				gp.heal(missingHp);
				updateChestplateColor(player);
			}

			private Location drawLine(Location start, Location end) {
				double distance = start.distance(end);
				Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(0.5d);

				for (double i = 0.0D; i < distance; i += 0.5) {
					start.add(vector);
					if (start.getWorld() == null) {
						continue;
					}
					if (!start.getBlock().getType().isAir()) {
						final Location cloned = start.add(0, 0.15, 0);
						start.getWorld().spawnParticle(Particle.FLAME, cloned, 3, 0.1, 0.1, 0.1, 0.02);
						return cloned;
					}
					//start.getWorld().playSound(start, Sound.BLOCK_BAMBOO_HIT, SoundCategory.RECORDS, 1.0f, 2.0f);
					start.getWorld().spawnParticle(Particle.REDSTONE, start, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
				}
				return start;
			}

		}.setCdSec(50)
				.setItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ1NzlmMWVhMzg2NDI2OWMyMTQ4ZDgyN2MwODg3YjBjNWVkNDNhOTc1YjEwMmEwMWFmYjY0NGVmYjg1Y2NmZCJ9fX0="));

	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		return new DamageOutput().setDamage(calculateDamage(input.getPlayer(), input.getDamage()));
	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		updateChestplateColor(input.getPlayer());
		return null;
	}

	private final ItemStack[] armorColors = {
			createChestplate(255, 128, 128),// 1
			createChestplate(255, 77, 77),  // 2
			createChestplate(255, 26, 26),  // 3
			createChestplate(179, 0, 0),    // 4
			createChestplate(102, 0, 0)     // 5
	};

	private ItemStack createChestplate(int red, int green, int blue) {
		return ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack();
	}

	private void updateChestplateColor(Player player) {
		final PlayerInventory inventory = player.getInventory();
		final IGamePlayer gp = GamePlayer.getPlayerSafe(player);
		final double missingHealth = gp.getMaxHealth() - gp.getHealth();

		if (isBetween(missingHealth, 0, 10)) {
			inventory.setChestplate(armorColors[0]);
		}
		else if (isBetween(missingHealth, 10, 20)) {
			inventory.setChestplate(armorColors[1]);
		}
		else if (isBetween(missingHealth, 20, 30)) {
			inventory.setChestplate(armorColors[2]);
		}
		else if (isBetween(missingHealth, 30, 40)) {
			inventory.setChestplate(armorColors[3]);
		}
		else {
			inventory.setChestplate(armorColors[4]);
		}
	}

	private boolean isBetween(double value, double min, double max) {
		return value >= min && value < max;
	}

	// 10% DMG per 20% <3
	public double calculateDamage(Player player, double damage) {
		final IGamePlayer gp = GamePlayer.getPlayerSafe(player);
		final double health = gp.getHealth();
		final double maxHealth = gp.getMaxHealth();
		final double multiplier = ((maxHealth - health) / 10);
		final double addDamage = (damage * 30 / 100) * multiplier;

		return Math.max(damage + addDamage, damage);
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.FLOWER_ESCAPE.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.FLOWER_BREEZE.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.EXCELLENCY.getTalent();
	}
}
