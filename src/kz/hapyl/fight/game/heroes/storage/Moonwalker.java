package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.AbstractGamePlayer;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.PlayerElement;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.ClassEquipment;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.TalentHandle;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.game.ui.UIComponent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class Moonwalker extends Hero implements PlayerElement, UIComponent {

	public Moonwalker() {
		super("Moonwalker");
		this.setInfo("A traveller from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
		this.setItem(Material.END_STONE);

		final ClassEquipment equipment = this.getEquipment();
		equipment.setHelmet(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNmOGZiZDc2NTg2OTIwYzUyNzM1MTk5Mjc4NjJmZGMxMTE3MDVhMTg1MWQ0ZDFhYWM0NTBiY2ZkMmIzYSJ9fX0="
		);
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
				.setInfo(
						"A unique bow made of unknown materials, seems to have two firing modes.__&e&lLEFT &e&lCLICK &7to fire quick arrow that deals 50% of normal damage."
				)
				.setDamage(7.0)
				.setId("MOON_WEAPON"));

		// moved to it's own class because it was unreadable lol
		this.setUltimate(new UltimateTalent(
				"Moonteorite",
				String.format(
						"Summons meteorite at the &etarget &7location. Upon landing, creates huge explosion dealing massive damage and applying &6&lCorrosion &7for &b%ss&7.",
						BukkitUtils.roundTick(corrosionTime)
				),
				80
		).setCdSec(45).setItem(Material.END_STONE_BRICKS));

	}

	private final int corrosionTime = 130; //update this in lore manually if changed
	private final double meteoriteExplosionRadius = 8.5d;

	@Nullable
	private Block getTargetBlock(Player player) {
		return player.getTargetBlockExact(25);
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getTargetBlock(player) != null;
	}

	@Override
	public String predicateMessage() {
		return "Not a valid block!";
	}

	public void createBlob(Location center, boolean last) {
		PlayerLib.spawnParticle(center, Particle.LAVA, 10, 1, 1, 1, 0);

		// ** Prev Clear
		this.clearTrash(center.clone());

		// ** Spawn
		center.subtract(1, 0, 1);

		final Set<Block> savedBlocks = new HashSet<>();

		//inner
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE_BRICKS);
				// only save the last iteration
				if (last) {
					savedBlocks.add(block);
				}
			}
		}

		//outer
		center.add(0, 1, 0);
		fillOuter(center, last ? savedBlocks : null);

		//outer 2
		center.subtract(0, 2, 0);
		fillOuter(center, last ? savedBlocks : null);

		if (last) {
			for (Block savedBlock : savedBlocks) {
				savedBlock.getState().update(false, false);
			}
			savedBlocks.clear();
		}

	}

	private Block sendChange(Location location, Material material) {
		final BlockData data = material.createBlockData();
		Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, data));
		return location.getBlock();
	}

	private void fillOuter(Location center, Set<Block> blocks) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i == 0 || i == 2) && j != 1) {
					continue;
				}
				final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE);
				if (blocks != null) {
					blocks.add(block);
				}
			}
		}
	}

	private void clearTrash(Location center) {
		center.add(0, 2, 0);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i == 0 || i == 2) && j != 1) {
					continue;
				}
				center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
			}
		}

		center.subtract(0, 1, 0);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (((i == 1 || i == 2) && j == 2) || (i == 2 && j == 1)) {
					continue;
				}
				center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
			}
		}

		center.subtract(0, 1, 0);
		center.clone().subtract(1, 0, 0).getBlock().getState().update(false, false);
		center.clone().subtract(0, 0, 1).getBlock().getState().update(false, false);
	}

	private void explode(Player executor, Location location) {
		final World world = location.getWorld();
		if (world == null) {
			throw new NullPointerException("world is null");
		}

		// fx
		world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1, 0, 0, 0, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, location, 15, 5, 2, 5, 0);

		world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 50, 0.0f);
		world.playSound(location, Sound.ENTITY_WITHER_HURT, 50, 0.25f);
		world.playSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 50, 0.5f);

		Utils.getPlayersInRange(location, meteoriteExplosionRadius).forEach(player -> {
			final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
			gp.addEffect(GameEffectType.CORROSION, corrosionTime, true);
			gp.damage(50.0d);
		});
	}

	@Override
	public void useUltimate(Player player) {
		final int distance = 16;
		final Location playerLocation = getTargetBlock(player).getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
		final Location startLocation = playerLocation.clone().add(distance, distance, distance);

		PlayerLib.playSound(player, Sound.ENTITY_WITHER_DEATH, 0.0f);

		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {

				if (tick++ >= distance + 1) {
					Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(Sound.ENTITY_WITHER_DEATH));
					explode(player, playerLocation);
					this.cancel();
					return;
				}

				Utils.getPlayersInRange(playerLocation, 8.5d)
						.forEach(target -> Chat.sendTitle(target, "&4&l⚠", "&cMeteorite Warning!", 0, 5, 5));

				Geometry.drawCircle(playerLocation, 10, Quality.NORMAL, new WorldParticle(Particle.CRIT));
				Geometry.drawCircle(playerLocation, 10.25, Quality.HIGH, new WorldParticle(Particle.SNOW_SHOVEL));

				createBlob(startLocation.clone(), (tick == distance + 1));
				startLocation.subtract(1, 1, 1);

			}
		}.runTaskTimer(5, 2);

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

	@Override
	public String getString(Player player) {
		final int bombs = TalentHandle.MOON_SLITE_BOMB.getBombsSize(player);
		return "&e■ &l" + bombs;
	}
}
