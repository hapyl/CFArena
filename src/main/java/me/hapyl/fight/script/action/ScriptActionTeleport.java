package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ScriptActionTeleport implements ScriptAction {

    private final BoundingBoxCollector boundingBox;
    private final Location location;

    ScriptActionTeleport(BoundingBoxCollector boundingBox, Location location) {
        this.boundingBox = boundingBox;
        this.location = location;
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        boundingBox.collectPlayers().forEach(player -> player.teleport(location));
    }

    @Override
    public String toString() {
        return "%s->%s".formatted(CFUtils.cuboidToString(boundingBox), BukkitUtils.locationToString(location));
    }
}