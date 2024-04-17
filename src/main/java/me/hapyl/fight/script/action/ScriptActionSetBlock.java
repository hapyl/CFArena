package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class ScriptActionSetBlock implements ScriptAction {

    private final Location location;
    private final Material material;

    ScriptActionSetBlock(Location location, Material material) {
        this.location = location;
        this.material = material;
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        location.getBlock().setType(material, false);
    }

    @Override
    public String toString() {
        return BukkitUtils.locationToString(location) + "=>" + material.name();
    }

}