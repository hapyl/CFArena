package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.event.DamageInput;
import kz.hapyl.fight.event.DamageOutput;
import kz.hapyl.fight.game.EnumDamageCause;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.EffectType;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class Shark extends Hero implements Listener {

	private final Map<Player, Long> lastCritMap = new HashMap<>();

	public Shark() {
		super(
				"Shark",
				"Strong warrior from the &bDepth of Waters&7... not well versed in on-land fights but don't let it touch the water or you'll regret it."
		);
		this.setItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0="
		);

		final ClassEquipment equipment = getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0N2U3ZTgyNzFmNTczOTY5ZjJkYTczNGM0MTI1ZjkzYjI4NjRmYjUxZGI2OWRhNWVjYmE3NDg3Y2Y4ODJiMCJ9fX0="
		);
		equipment.setChestplate(116, 172, 204);
		equipment.setLeggings(116, 172, 204);
		equipment.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(116, 172, 204)).addEnchant(Enchantment.DEPTH_STRIDER, 5).cleanToItemSack());

		setWeapon(new Weapon(Material.QUARTZ)
				.setName("Claws")
				.setInfo("Using one's claws is the better idea than using a stick, don't you think so?")
				.setDamage(7.0d));

		setUltimate(new UltimateTalent(
				"Ocean Madness",
				"Creates a &bShark Aura &7that follow you for {duration} and imitates water.",
				70
		).setItem(Material.WATER_BUCKET).setDuration(120).setSound(Sound.AMBIENT_UNDERWATER_ENTER, 0.0f).setCdSec(60));

	}

	@Override
	public void useUltimate(Player player) {
		setState(player, true, getUltimateDuration());

		new GameTask() {
			private int tick = getUltimateDuration();

			@Override
			public void run() {
				if (tick < 0) {
					setState(player, false, 0);
					this.cancel();
					return;
				}

				final Location location = player.getLocation();

				// Fx
				Geometry.drawCircle(location, 3.5d, Quality.HIGH, new WorldParticle(Particle.WATER_DROP));
				Geometry.drawCircle(location, 1.0d, Quality.VERY_HIGH, new WorldParticle(Particle.WATER_SPLASH));

				--tick;
			}
		}.runTaskTimer(0, 1);

	}

	@EventHandler()
	public void handlePlayerMove(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		if (!validatePlayer(player, Heroes.SHARK) || isUsingUltimate(player)) {
			return;
		}

		setState(player, player.isInWater(), 10);
	}

	public void setState(Player player, boolean state, int duration) {
		if (state) {
			player.setWalkSpeed(0.6f);
			PlayerLib.addEffect(player, EffectType.STRENGTH, duration, 2);
			PlayerLib.addEffect(player, EffectType.RESISTANCE, duration, 1);
		}
		else {
			player.setWalkSpeed(0.2f);
		}
	}

	@Override
	public DamageOutput processDamageAsDamager(DamageInput input) {
		final Player player = input.getPlayer();
		final LivingEntity entity = input.getEntity();
		if (!canCrit(player) || entity == player) {
			return null;
		}

		if (ThreadRandom.nextFloatAndCheckBetween(0.9f, 1.0f)) {
			lastCritMap.put(player, System.currentTimeMillis());
			performCriticalHit(player, entity.getLocation());
		}

		return null;
	}

	private boolean canCrit(Player player) {
		final Long lastCrit = lastCritMap.getOrDefault(player, 0L);
		return lastCrit == 0 || System.currentTimeMillis() - lastCrit >= 2500;
	}

	public void performCriticalHit(Player player, Location location) {
		final EvokerFangs fangs = Entities.EVOKER_FANGS.spawn(location);
		fangs.setOwner(player);

		Utils.getEntitiesInRange(location, 2.0d).forEach(entity -> {
			if (entity == player) {
				return;
			}
			GamePlayer.damageEntity(entity, 5.0d, player, EnumDamageCause.FEET_ATTACK);
		});
		Geometry.drawCircle(location, 2.0d, Quality.HIGH, new WorldParticle(Particle.CRIT_MAGIC));
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.SUBMERGE.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.CLAW_CRITICAL.getTalent();
	}
}
