package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.math.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;

public class ScriptActionFill implements ScriptAction {

    private final Cuboid cuboid;
    private final BlockData data;

    ScriptActionFill(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, BlockData data) {
        this.cuboid = new Cuboid(fromX, fromY, fromZ, toX, toY, toZ);
        this.data = data;
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        cuboid.getBlocks().forEach(block -> block.setBlockData(data, false));
    }

    @Override
    public String toString() {
        return "%s=>%s".formatted(CFUtils.cuboidToString(cuboid), data.getAsString(true));
    }
}