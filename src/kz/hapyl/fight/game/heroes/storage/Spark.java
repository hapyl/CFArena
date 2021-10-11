package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.AbstractGamePlayer;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.PlayerElement;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.PackedParticle;
import kz.hapyl.fight.game.weapons.RangeWeapon;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Spark extends Hero implements PlayerElement {

	private final Map<Player, ArmorStand> markerLocation = new HashMap<>();

	public Spark() {
		super("Spark");
		this.setItem(Material.BLAZE_POWDER);
		this.setInfo("Strikes as fire with his fire abilities.");

		final ClassEquipment eq = this.getEquipment();
		eq.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRlMDk1MzMyNzIwMjE1Y2E5Yjg1ZTdlYWNkMWQwOTJiMTY5N2ZhZDM0ZDY5NmFkZDk0ZDNiNzA5NzY3MDJjIn19fQ==");
		eq.setChestplate(Color.ORANGE);
		eq.setLeggings(Color.RED);
		eq.setBoots(Color.ORANGE);

		this.setWeapon(new RangeWeapon(Material.STICK, "fire_weapon") {
			@Override
			public void onHit(LivingEntity entity) {
				entity.setFireTicks(10);
			}

			@Override
			public void onMove(Location location) {

			}
		}.setCooldown(30)
				.setSound(Sound.ENTITY_BLAZE_SHOOT, 1.75f)
				.setParticleHit(new PackedParticle(Particle.LAVA).setAmount(3).setSpeed(0.2f))
				.setParticleTick(new PackedParticle(Particle.FLAME).setSpeed(0.001f))
				.setDamage(8.0d)
				.setName("Fire Sprayer")
				.setLore("A long range weapon that shoots fire lasers! How cool is that..."));

		this.setUltimate(new UltimateTalent(
				"Run it Back",
				"Instantly place a marker at your current location for {duration}. Upon death or after duration ends, safely teleports to the marked location with health you had upon activating the ability.",
				80
		).setDuration(200).setItem(Material.TOTEM_OF_UNDYING).setCdSec(40));

	}

	@Override
	public void useUltimate(Player player) {
		final Location location = getSafeLocation(player.getLocation());
		final AbstractGamePlayer gp = GamePlayer.getPlayer(player);

		if (location == null) {
			return;
		}

		setUsingUltimate(player, true);
		final ArmorStand marker = Entities.ARMOR_STAND.spawn(location, me -> {
			me.setMaxHealth(gp.getHealth());
			me.setHealth(gp.getHealth());
			me.setGravity(false);
			me.setInvisible(true);
			me.setVisible(false);
			me.setFireTicks(getUltimateDuration());
		});

		markerLocation.put(player, marker);

		new GameTask() {
			private int tick = getUltimateDuration();

			@Override
			public void run() {
				// if already on rebirth, can happen when damage is called rebirth
				if (getMarker(player) == null) {
					this.cancel();
					return;
				}

				if (tick < 0) {
					rebirthPlayer(player);
					this.cancel();
					return;
				}

				// display how much time left
				// symbols => ■□
				final StringBuilder builder = new StringBuilder();
				for (int i = 0; i < 20; i++) {
					builder.append(Chat.format(i >= (tick / 10) ? "&c|" : "&a|"));
				}

				Chat.sendTitle(player, "", builder.toString(), 0, 10, 0);

				--tick;

				// fx
				PlayerLib.spawnParticle(player.getEyeLocation(), Particle.FLAME, 1, 0.5d, 0.0d, 0.5d, 0.01f);

				// fx at marker
				PlayerLib.spawnParticle(location, Particle.LANDING_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);
				PlayerLib.spawnParticle(location, Particle.DRIP_LAVA, 1, 0.2d, 0.2, 0.2d, 0.05f);

			}
		}.runTaskTimer(0, 1);

	}

	@Override
	public boolean predicateUltimate(Player player) {
		return isSafeLocation(player.getLocation());
	}

	@Override
	public String predicateMessage() {
		return "Location is not safe!";
	}

	private ArmorStand getMarker(Player player) {
		return markerLocation.get(player);
	}

	private boolean isSafeLocation(Location location) {
		return getSafeLocation(location) != null;
	}

	private Location getSafeLocation(Location location) {
		// start with a bit of Y offset
		location.add(0, 2, 0);
		for (int i = 0; i < 10; i++) {
			location.subtract(0, 1, 0);
			if (!location.getBlock().getType().isAir()) {
				return location;
			}
		}
		return null;
	}

	public void rebirthPlayer(Player player) {
		final ArmorStand stand = markerLocation.get(player);
		if (stand == null) {
			return;
		}

		final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
		final Location location = player.getLocation(); // location before tp

		player.setInvulnerable(true);
		gp.setHealth(stand.getHealth());
		final Location teleportLocation = stand.getLocation().add(0.0d, 1.0d, 0.0d);

		markerLocation.remove(player);
		stand.remove();

		player.teleport(teleportLocation);

		// fx
		PlayerLib.addEffect(player, PotionEffectType.SLOW, 20, 50);
		PlayerLib.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);
		PlayerLib.spawnParticle(location, Particle.FIREWORKS_SPARK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
		PlayerLib.spawnParticle(location, Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);
		Chat.sendTitle(player, "&6🔥", "&eOn Rebirth...", 5, 10, 5);

		GameTask.runLater(() -> {
			player.setInvulnerable(false);
		}, 20);

	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		final Player player = input.getPlayer();
		if (input.getDamage() >= GamePlayer.getPlayer(player).getHealth()) {
			rebirthPlayer(player);
			setUsingUltimate(player, false);
			return new DamageOutput().setCancelDamage(true);
		}
		return null;
	}

	@Override
	public void onStart(Player player) {
		PlayerLib.addEffect(player, PotionEffectType.FIRE_RESISTANCE, 999999, 1);
	}

	@Override
	public DamageOutput processDamageAsVictim(DamageInput input) {
		boolean validCause = switch (input.getDamageCause()) {
			case FIRE, FIRE_TICK, LAVA -> true;
			default -> false;
		};
		if (validCause) {
			input.getPlayer().setFireTicks(0);
			return new DamageOutput().setCancelDamage(true);
		}
		return null;
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.SPARK_MOLOTOV.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.SPARK_FLASH.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.FIRE_GUY.getTalent();
	}
}
