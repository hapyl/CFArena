package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.Talents;
import kz.hapyl.fight.game.talents.storage.extra.GrimoireTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class BlackHole extends Talent implements GrimoireTalent {
	public BlackHole() {
		super("Black Hole");
		this.setInfo(String.format(
				"Creates a black hole at your target block. Pulling enemies in and dealing %s damage per second based on &cGrimoire &7level.",
				formatValues()
		));
		this.setItem(Material.BLACK_CANDLE);
		this.setAutoAdd(false);
	}

	@Override
	public int getGrimoireCd() {
		return 60;
	}

	@Override
	protected Response execute(Player player) {
		if (HeroHandle.LIBRARIAN.hasICD(player)) {
			return ERROR;
		}

		final Block block = player.getTargetBlockExact(10);

		if (block == null) {
			return Response.error("&cNo valid target block!");
		}

		final Location location = block.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
		PlayerLib.spawnParticle(location, Particle.GLOW, 1, 0, 0, 0, 0);

		final double suckRadius = 3.0d;
		GameTask.runTaskTimerTimes((task, tick) -> {

			// FX
			final double tick60 = tick / 60d;
			final double tick16 = tick / 16d;
			final double tick120 = tick / 120d;

			for (double i = 0; i < Math.PI * 2; i += (Math.PI / 4)) {
				final double x = (tick16 * Math.sin(i + tick60));
				final double z = (tick16 * Math.cos(i + tick60));
				location.add(x, tick120, z);
				PlayerLib.spawnParticle(location, Particle.SPELL_WITCH, 1, 0, 0, 0, 0);
				location.subtract(x, tick120, z);
			}

			Utils.getEntitiesInRange(location, suckRadius).forEach(entity -> {
				if (entity == player) {
					return;
				}

				final Location entityLocation = entity.getLocation();
				entity.setVelocity(location.toVector().subtract(entityLocation.toVector()).multiply(0.2d));

				if (tick % 20 == 0) {
					GamePlayer.damageEntity(entity, getCurrentValue(HeroHandle.LIBRARIAN.getGrimoireLevel(player)));
					PlayerLib.spawnParticle(entityLocation, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
					PlayerLib.playSound(entityLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);
				}

			});

			PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, (float)Math.min(1.2f + (tick120), 2.0f));

		}, 1, 60);

		HeroHandle.LIBRARIAN.removeSpellItems(player, Talents.BLACK_HOLE);
		return Response.OK;
	}

	@Override
	public double[] getValues() {
		return new double[]{5.0d, 7.5d, 10.0d, 12.5d};
	}
}
