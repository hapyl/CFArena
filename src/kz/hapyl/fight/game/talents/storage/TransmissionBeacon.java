package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class TransmissionBeacon extends Talent {
	public TransmissionBeacon() {
		super(
				"Transmission Beacon",
				"Place the beacon somewhere hidden from your opponents. Use your &bultimate &7to instantly teleport to it's location and collect it.__&c&lThe beacon can be destroyed!____&9Cooldown if Destroyed: &l30s",
				Type.COMBAT
		);
		this.setItem(Material.BEACON);
	}

	@Override
	public Response execute(Player player) {
		if (HeroHandle.ENDER.hasBeacon(player)) {
			return Response.error("Beacon is already present!");
		}

		final Block block = player.getTargetBlockExact(5);
		if (block == null || !isSafeLocation(block)) {
			return Response.error("Location is not safe!");
		}

		final Location location = block.getRelative(BlockFace.UP).getLocation();
		HeroHandle.ENDER.setBeaconLocation(player, location);

		return Response.OK;
	}

	private boolean isSafeLocation(Block block) {
		final Block relative = block.getRelative(BlockFace.UP);
		return relative.getType().isAir() && relative.getRelative(BlockFace.UP).getType().isAir();
	}

}
