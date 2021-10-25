package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.PackedParticle;
import kz.hapyl.fight.game.weapons.RangeWeapon;
import kz.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class KillingMachine extends Hero {
	private final int weaponCd = 35;

	public KillingMachine() {
		super("War Machine");
		this.setInfo("A machine of war that was left for scrap, until now...");
		this.setItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMyZjNkNWQ2MmZkOWJlNmQ2NTRkMzE0YzEyMzM5MGFiZmEzNjk4ZDNkODdjMTUxNmE0NTNhN2VlNGZjYmYifX19"
		);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMyZjNkNWQ2MmZkOWJlNmQ2NTRkMzE0YzEyMzM5MGFiZmEzNjk4ZDNkODdjMTUxNmE0NTNhN2VlNGZjYmYifX19"
		);
		equipment.setChestplate(Material.CHAINMAIL_CHESTPLATE);
		equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
		equipment.setBoots(Material.CHAINMAIL_BOOTS);

		this.setWeapon(new RangeWeapon(Material.IRON_HORSE_ARMOR, "km_weapon") {
			@Override
			public void onHit(LivingEntity entity) {

			}

			@Override
			public void onMove(Location location) {

			}

			@Override
			public void onShoot(Player player) {
				startCooldown(player, isUsingUltimate(player) ? weaponCd / 2 : weaponCd);
			}
		}.setSound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.4f)
				.setParticleTick(new PackedParticle(Particle.END_ROD))
				.setParticleHit(new PackedParticle(Particle.END_ROD, 1, 0, 0, 0, 0.1f))
				.setDamage(5.0d)
				.setName("Rifle"));

		this.setUltimate(new UltimateTalent(
				"Overload",
				"Overload yourself for {duration}. While overloaded, your fire-rate is increased by &b100% &7and all opponents are highlighted.",
				60
		).setDurationSec(12).setItem(Material.LIGHTNING_ROD).setSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.0f));

	}

	@Override
	public void useUltimate(Player player) {
		Manager.current().getCurrentGame().getAlivePlayers().forEach(gamePlayer -> {
			if (gamePlayer.compare(player)) {
				return;
			}

			new Glowing(gamePlayer.getPlayer(), ChatColor.RED, getUltimateDuration()).addViewer(player);
		});
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.LASER_EYE.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.GRENADE.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}
}
