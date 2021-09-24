package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.PlayerElement;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.heroes.storage.extra.MoonwalkerUltimate;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Moonwalker extends Hero implements PlayerElement {

	public Moonwalker() {
		super("Moonwalker");
		this.setInfo("A traveller from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
		this.setItem(Material.END_STONE);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNmOGZiZDc2NTg2OTIwYzUyNzM1MTk5Mjc4NjJmZGMxMTE3MDVhMTg1MWQ0ZDFhYWM0NTBiY2ZkMmIzYSJ9fX0=");
		equipment.setChestplate(199, 199, 194);
		equipment.setLeggings(145, 145, 136);
		equipment.setBoots(53, 53, 49);

		this.setWeapon(new Weapon(Material.BOW) {
			@Override
			public void onLeftClick(Player player, ItemStack item) {
				if (player.hasCooldown(Material.BOW)) {
					return;
				}
				final Arrow arrow = player.launchProjectile(Arrow.class);
				arrow.setDamage(this.getDamage() / 2.0d);
				arrow.setCritical(false);
				arrow.setShooter(player);
				player.setCooldown(Material.BOW, 20);
			}
		}.setName("Stinger")
				.setLore("A unique bow made of unknown materials, seems to have two firing modes.__&e&lLEFT &e&lCLICK &7to fire quick arrow that deals 50% of normal damage.")
				.setDamage(7.0)
				.setId("MOON_WEAPON"));

		// moved to it's own class because it was unreadable lol
		this.setUltimate(new MoonwalkerUltimate());

	}

	@Override
	public void onStart(Player player) {
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
		PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 999999, 2);
	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				Heroes.MOONWALKER.getAlivePlayers().forEach(gp -> {
					final Player player = gp.getPlayer();
					final Block targetBlock = getTargetBlock(player);
					if (!player.isSneaking() || targetBlock == null) {
						return;
					}
					final Location location = targetBlock.getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
					for (int i = 0; i < 10; i++) {
						player.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
						location.add(0, 0.15, 0);
					}

				});
			}
		}.runTaskTimer(0, 2);
	}

	private Block getTargetBlock(Player player) {
		return player.getTargetBlockExact(20);
	}

	@Override
	public Talent getFirstTalent() {
		return Talents.MOONSLITE_PILLAR.getTalent();
	}

	@Override
	public Talent getSecondTalent() {
		return Talents.MOONSLITE_BOMB.getTalent();
	}

	@Override
	public Talent getPassiveTalent() {
		return Talents.TARGET.getTalent();
	}
}
