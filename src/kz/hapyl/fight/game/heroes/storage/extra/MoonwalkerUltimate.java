package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.IGamePlayer;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

// yes this class is so big I have to put it here lol
public class MoonwalkerUltimate extends UltimateTalent {

	private final int corrosionTime = 130; //update this in lore manually if changed
	private final double meteoriteExplosionRadius = 8.5d;

	public MoonwalkerUltimate() {
		super("Moonteorite", "Summons meteorite at the &etarget &7location. Upon landing, creates huge explosion dealing massive damage and applying &6&lCorrosion &7for &b6.5s&7.", 80);
		this.setCdSec(45);
		this.setItem(Material.END_STONE_BRICKS);
	}

	@Nullable
	private Block getTargetBlock(Player player) {
		return player.getTargetBlockExact(25);
	}

	@Override
	public boolean predicate(Player player) {
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
			final IGamePlayer gp = GamePlayer.getPlayerSafe(player);
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

}