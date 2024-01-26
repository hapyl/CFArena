package me.hapyl.fight.script.parser;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptException;
import me.hapyl.fight.script.ScriptLine;
import me.hapyl.fight.script.ScriptRunner;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SetBlockParser implements ScriptLineParser {
    @Override
    public ScriptAction parse(@Nonnull ScriptLine line) {
        if (!line.isKeyMatches("setblock")) {
            return null;
        }

        final int x = line.getValueOrThrow(1, Integer.class, v -> "Expected an integer as 'x', got: " + v);
        final int y = line.getValueOrThrow(2, Integer.class, v -> "Expected an integer as 'y', got: " + v);
        final int z = line.getValueOrThrow(3, Integer.class, v -> "Expected an integer as 'z', got: " + v);
        final Material material = line.getValueOrThrow(4, Material.class, v -> "Expected a material, got: " + v);

        if (!material.isBlock()) {
            throw new ScriptException("Material must be a block!");
        }

        return new SetBlockAction(new Location(BukkitUtils.defWorld(), x, y, z), material);
    }

    static class SetBlockAction implements ScriptAction {

        private final Location location;
        private final Material material;

        public SetBlockAction(Location location, Material material) {
            this.location = location;
            this.material = material;
        }

        @Override
        public boolean execute(@Nonnull ScriptRunner runner) {
            return false;
        }

        @Override
        public String toString() {
            return location.toString() + "->" + material;
        }
    }
}
