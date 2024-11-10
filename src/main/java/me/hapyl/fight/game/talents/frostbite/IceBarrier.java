package me.hapyl.fight.game.talents.frostbite;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Direction;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class IceBarrier extends Talent {

    @DisplayField protected final int buildDelay = 4;
    @DisplayField(suffix = "blocks") protected final double radius = 3.5d;
    @DisplayField protected final double healingPerTick = 2.0d;

    public IceBarrier() {
        super(Key.empty(), "Ice Barrier");

        setDescription("""
                Creates an ice wall at your &etarget&7 location that melts overtime.
                
                While active, the wall will periodically unleash freezing energy, healing yourself and slowing enemies down.
                """);

        setItem(Material.PACKED_ICE);
        setCooldownSec(30);
        setDuration(Tick.fromSecond(15));
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (true) {
            return Response.OK;
        }

        final Location targetLocation = getBuildLocation(player);

        if (targetLocation == null) {
            return Response.error("No valid block in sight!");
        }

        final Direction direction = Direction.getDirection(player.getLocation());
        final boolean isWestEast = direction.isWestOrEast();

        final Location location = targetLocation.subtract((isWestEast ? 0 : 2), 0, (isWestEast ? 2 : 0));

        return Response.OK;
    }

    private Location getBuildLocation(GamePlayer player) {
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
