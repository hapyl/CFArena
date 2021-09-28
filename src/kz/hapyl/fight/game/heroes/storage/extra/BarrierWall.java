package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BarrierWall {

	private final List<Block> blocks;
	private final Location location;
	private final Player player;
	private boolean built;

	public BarrierWall(Player player, Location location) {
		this.blocks = new ArrayList<>();
		this.player = player;
		this.location = location;
		this.built = false;
	}

	public BarrierWall add(Block block) {
		blocks.add(block);
		return this;
	}

	public void build() {
		for (Block block : this.blocks) {
			block.setType(Material.ICE, false);
		}
		built = true;
	}

	public void buildSmooth(int delayEachTime, int destroyAfter) {
		final List<Block> blocks = getBlocks();
		if (blocks.isEmpty()) {
			return;
		}

		iterateBlocks(0, 5, delayEachTime);
		iterateBlocks(5, 10, delayEachTime * 2);
		iterateBlocks(10, 15, delayEachTime * 3, () -> {
			built = true;
			startTicking();
			if (destroyAfter > 0) {
				decay(destroyAfter);
			}
		});

	}

	private void startTicking() {
		new GameTask() {
			private final double radius = 3.5d;

			@Override
			public void run() {
				if (!built) {
					this.cancel();
					return;
				}

				if (player.getLocation().distance(location) <= radius) {
					GamePlayer.getPlayer(player).heal(2.0d);
					PlayerLib.addEffect(player, PotionEffectType.SPEED, 30, 1);
				}

				Geometry.drawCircle(location, radius, Quality.HIGH, new WorldParticle(Particle.FALLING_WATER));
				Geometry.drawCircle(location, radius, Quality.HIGH, new WorldParticle(Particle.BUBBLE_POP));

			}
		}.runTaskTimer(0, 20);
	}

	private void iterateBlocks(int start, int end, int delay) {
		iterateBlocks(start, end, delay, null);
	}

	private void iterateBlocks(int start, int end, int delay, @Nullable Runnable runnable) {
		GameTask.runLater(() -> {
			for (int i = start; i < end; i++) {
				if (blocks.size() <= i) {
					break;
				}
				setBlock(blocks.get(i));
				Nulls.runIfNotNull(runnable, Runnable::run);
			}
		}, delay);
	}

	private void setBlock(Block block) {
		PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 0.8f);
		PlayerLib.playSound(block.getLocation(), Sound.ENTITY_SNOW_GOLEM_HURT, 1.6f);
		block.setType(Material.ICE, false);
	}

	public void destroy() {
		for (Block block : this.blocks) {
			block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 10, 2f);
			block.setType(Material.AIR, false);
		}
		this.blocks.clear();

		built = false;
		HeroHandle.FREAZLY.getBarrierWallMap().remove(player);
	}

	private void decay(int destroyAfter) {
		if (!built) {
			return;
		}

		// idk where is this coming from
		final int delay = destroyAfter / 4 * 20;

		new GameTask() {
			private int currentTick = 0;

			@Override
			public void run() {

				for (Block block : blocks) {
					block.setType(Material.FROSTED_ICE, false);
					final Ageable blockData = (Ageable)block.getBlockData();
					blockData.setAge(Math.min(currentTick, 3));
					block.setBlockData(blockData, false);
				}

				if (currentTick++ > 3) {
					destroy();
					this.cancel();
				}
			}
		}.runTaskTimer(delay, delay);

	}

	public List<Block> getBlocks() {
		return this.blocks;
	}

}