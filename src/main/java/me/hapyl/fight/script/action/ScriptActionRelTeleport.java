package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ScriptActionRelTeleport implements ScriptAction {

    private final BoundingBoxCollector boundingBox;
    private final double[] relative;

    ScriptActionRelTeleport(BoundingBoxCollector boundingBox, double x, double y, double z) {
        this.boundingBox = boundingBox;
        this.relative = new double[] { x, y, z };
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        boundingBox.collectPlayers().forEach(player -> {
            final Location playerLocation = player.getLocation();
            playerLocation.add(relative[0], relative[1], relative[2]);

            player.teleport(playerLocation);
        });
    }

    @Override
    public String toString() {
        return "%s->%s".formatted(CFUtils.cuboidToString(boundingBox), Arrays.toString(relative));
    }
}