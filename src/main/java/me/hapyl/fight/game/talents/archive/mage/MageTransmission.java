package me.hapyl.fight.game.talents.archive.mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class MageTransmission extends Talent {

	@DisplayField(suffix = "blocks") private final double maxDistance = 30.0d;

	public MageTransmission() {
		super("Transmission", "Instantly teleport to your target block, but lose ability to move for a short duration.", Type.COMBAT);

		setItem(Material.ENDER_PEARL);
        setCooldownSec(30);
	}

	@Override
	public Response execute(Player player) {
		final Location location = getTargetLocation(player);
		if (location == null) {
			return Response.error("No valid block in sight!");
		}
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		if (location.distance(player.getLocation()) >= maxDistance) {
			return Response.error("Too far away!");
		}

		if (!location.getBlock().getType().isAir() || location.getBlock().getRelative(BlockFace.UP).getType().isOccluding()) {
			return Response.error("Location is not safe!");
		}

		player.teleport(location);
		PlayerLib.addEffect(player, PotionEffectType.SLOW, 20, 10);
		PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.65f);
		if (location.getWorld() != null) {
			location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
		}

		return Response.OK;
	}

	private Location getTargetLocation(Player player) {
		final Block block = player.getTargetBlockExact(30);
		if (block == null) {
			return null;
		}
		return block.getRelative(BlockFace.UP).getLocation();
	}


}
