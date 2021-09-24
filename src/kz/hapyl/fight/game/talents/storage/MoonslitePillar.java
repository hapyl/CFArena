package kz.hapyl.fight.game.talents.storage;

import com.google.common.collect.Maps;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class MoonslitePillar extends Talent {

	private final Map<Player, Location> pillars = Maps.newHashMap();

	public MoonslitePillar() {
		super("Moonsplite Pillar", "Raises a pillar at &etarget &7location for &b10s &7that pulses in set intervals, damaging enemies and healing yourself. You can only have 1 pillar at the time.", Type.COMBAT);
		this.setItem(Material.BONE);
		this.setCdSec(30);
	}

	@Override
	public Response execute(Player player) {
		final Block block = getTargetBlock(player);
		if (block == null) {
			return Response.error("No valid target block!");
		}

		final Location location = block.getLocation();
		if (!canFit(location)) {
			return Response.error("Cannot fit pillar!");
		}

		if (pillars.containsKey(player)) {
			destroyPillar(location);
		}

		pillars.put(player, location);
		raisePillar(location);

		final int period = 5;
		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {
				if ((tick += period) >= (10 * 20)) {
					destroyPillar(location);
					pillars.remove(player);
					this.cancel();
					return;
				}

				// pulse
				if (tick % 15 == 0) {
					pulsePillar(location, player);
				}

			}
		}.runTaskTimer(0, period);

		return Response.OK;
	}

	private void raisePillar(Location location) {
		location.getBlock().setType(Material.END_STONE_BRICKS, false);
		location.getBlock().getRelative(BlockFace.UP).setType(Material.END_STONE_BRICKS, false);
		location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.END_STONE, false);

		PlayerLib.playSound(location, Sound.BLOCK_PISTON_EXTEND, 0.25f);
	}

	private void destroyPillar(Location location) {
		if (location == null || location.getBlock().getType().isAir()) {
			return;
		}

		location.getBlock().setType(Material.AIR, false);
		location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
		location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR, false);

		//fx
		PlayerLib.playSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);

		if (location.getWorld() == null) {
			throw new NullPointerException("world null?");
		}
		location.getWorld().spawnParticle(Particle.SPIT, location.clone().add(0, 2, 0), 15, 0, 1, 0, 0.05);

	}

	private void pulsePillar(Location location, Player owner) {
		final double effectRange = 2.5d;
		final BlockData blockData = Material.END_STONE.createBlockData();
		Geometry.drawCircle(location, effectRange, Quality.NORMAL, new Draw(Particle.BLOCK_DUST) {
			@Override
			public void draw(Location location) {
				final World world = location.getWorld();
				if (world != null) {
					world.spawnParticle(this.getParticle(), location, 0, 0, 0, 0, blockData);
				}
			}
		});
		PlayerLib.playSound(location, Sound.BLOCK_STONE_BREAK, 0.0f);
		Utils.getEntitiesInRange(location, effectRange).forEach(entity -> {
			if (entity == owner) {
				GamePlayer.getPlayerSafe(owner).heal(2.0d);
				PlayerLib.addEffect(owner, PotionEffectType.JUMP, 20, 2);
				PlayerLib.spawnParticle(owner.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
			}
			else {
				entity.damage(5.0d);
				entity.setVelocity(entity.getLocation().getDirection().multiply(-0.5).setY(0.25d));
			}
		});
	}

	@Override
	public void onStop() {
		this.pillars.values().forEach(this::destroyPillar);
		this.pillars.clear();
	}

	@Nullable
	public Location getPillar(Player player) {
		return this.pillars.getOrDefault(player, null);
	}

	private boolean canFit(Location location) {
		final Block block = location.getBlock();
		return block.getType().isAir() && block.getRelative(BlockFace.UP).getType().isAir() && block.getRelative(BlockFace.UP)
				.getRelative(BlockFace.UP)
				.getType()
				.isAir();
	}

	private Block getTargetBlock(Player player) {
		final Block block = player.getTargetBlockExact(7);
		if (block == null) {
			return null;
		}
		return block.getRelative(BlockFace.UP);
	}

}
