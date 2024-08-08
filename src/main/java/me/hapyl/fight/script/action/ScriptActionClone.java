package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ScriptActionClone implements ScriptAction {

    private final Cuboid cuboid;
    private final Location destination;

    ScriptActionClone(@Nonnull Cuboid cuboid, double x, double y, double z) {
        this.cuboid = cuboid;
        this.destination = BukkitUtils.defLocation(x, y, z);
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        cuboid.cloneBlocksTo(destination, false);
    }

    @Override
    public String toString() {
        return "%s->%s".formatted(CFUtils.cuboidToString(cuboid), BukkitUtils.locationToString(destination));
    }


}