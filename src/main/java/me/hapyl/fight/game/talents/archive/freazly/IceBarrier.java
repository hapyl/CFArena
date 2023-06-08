package me.hapyl.fight.game.talents.archive.freazly;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.MappedTalent;
import me.hapyl.fight.util.Direction;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class IceBarrier extends MappedTalent<BarrierWall> {

    @DisplayField protected final int buildDelay = 4;
    @DisplayField(suffix = "blocks") protected final double radius = 3.5d;
    @DisplayField protected final double healingPerTick = 2.0d;

    public IceBarrier() {
        super("Ice Barrier");

        setDescription("""
                Creates an ice wall at your &etarget&7 location that melts overtime.
                                
                While active, the wall will periodically unleash freezing energy, healing yourself and slowing enemies down.
                """);

        setItem(Material.PACKED_ICE);
        setCooldownSec(30);
        setDuration(Tick.fromSecond(15));
    }

    @Override
    public Response execute(Player player) {
        if (isExists(player)) {
            return Response.error("Already created!");
        }

        final Location targetLocation = getBuildLocation(player);

        if (targetLocation == null) {
            return Response.error("No valid block in sight!");
        }

        final Direction direction = Direction.getDirection(player);
        final boolean isEastWest = direction.isEastWest();

        final BarrierWall wall = createMapped(player, new BarrierWall(player, targetLocation.add(0.5d, 0.0d, 0.5d), this));
        final Location location = targetLocation.subtract((isEastWest ? 0 : 2), 0, (isEastWest ? 2 : 0));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                final Block blockToChange = location.add(isEastWest ? 0 : j, i, isEastWest ? j : 0).getBlock();

                if (blockToChange.getType().isAir()) {
                    wall.add(blockToChange);
                }

                location.subtract(isEastWest ? 0 : j, i, isEastWest ? j : 0);
            }
        }

        wall.build();

        return Response.OK;
    }

    private Location getBuildLocation(Player player) {
        final Block target = player.getTargetBlockExact(5);

        if (target == null) {
            return null;
        }

        final Block up = target.getRelative(BlockFace.UP);

        if (!up.getType().isAir()) {
            return null;
        }

        return up.getLocation();
    }


}
