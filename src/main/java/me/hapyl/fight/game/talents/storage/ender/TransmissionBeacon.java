package me.hapyl.fight.game.talents.storage.ender;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.Ender;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class TransmissionBeacon extends Talent {

    @DisplayField private final int cooldownIfDestroyed = 600;

    public TransmissionBeacon() {
        super("Transmission Beacon");

        addDescription(
                "Place the beacon somewhere hidden from your opponents. Use your &bultimate &7to instantly teleport to it's location and collect it.__&c&lThe beacon can be destroyed!"
        );

        setItem(Material.BEACON);
    }

    public int getDestroyCd() {
        return cooldownIfDestroyed;
    }

    @Override
    public Response execute(Player player) {
        if (Heroes.ENDER.getHero(Ender.class).hasBeacon(player)) {
            return Response.error("Beacon is already present!");
        }

        final Block block = player.getTargetBlockExact(5);

        if (block == null || !isSafeLocation(block)) {
            return Response.error("Location is not safe!");
        }

        final Location location = block.getRelative(BlockFace.UP).getLocation();
        Heroes.ENDER.getHero(Ender.class).setBeaconLocation(player, location);

        return Response.OK;
    }

    private boolean isSafeLocation(Block block) {
        final Block relative = block.getRelative(BlockFace.UP);
        return relative.getType().isAir() && relative.getRelative(BlockFace.UP).getType().isAir();
    }

}
