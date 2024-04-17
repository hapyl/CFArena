package me.hapyl.fight.script.action;

import me.hapyl.fight.script.Script;
import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.spigotutils.module.math.Cuboid;
import me.hapyl.spigotutils.module.util.Builder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScriptActionBuilder implements Builder<Script> {

    private final Script script;

    public ScriptActionBuilder(Script script) {
        this.script = script;
    }

    // *=* Block Manipulations *=* //

    public ScriptActionBuilder clone(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double destX, double destY, double destZ) {
        return clone(new Cuboid(minX, minY, minZ, maxX, maxY, maxZ), destX, destY, destZ);
    }

    public ScriptActionBuilder clone(@Nonnull Cuboid from, double x, double y, double z) {
        script.push(new ScriptActionClone(from, x, y, z));
        return this;
    }

    public ScriptActionBuilder setBlock(double x, double y, double z, @Nonnull Material material) {
        script.push(new ScriptActionSetBlock(BukkitUtils.defLocation(x, y, z), material));
        return this;
    }

    public ScriptActionBuilder fill(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, @Nonnull BlockData data) {
        script.push(new ScriptActionFill(fromX, fromY, fromZ, toX, toY, toZ, data));
        return this;
    }

    public ScriptActionBuilder fill(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, @Nonnull Material material) {
        return fill(fromX, fromY, fromZ, toX, toY, toZ, material.createBlockData());
    }

    // *=* Player Manipulations *=* //

    public ScriptActionBuilder teleport(@Nonnull BoundingBoxCollector box, double x, double y, double z) {
        script.push(new ScriptActionTeleport(box, BukkitUtils.defLocation(x, y, z)));
        return this;
    }

    public ScriptActionBuilder relTeleport(@Nonnull BoundingBoxCollector box, double x, double y, double z) {
        script.push(new ScriptActionRelTeleport(box, x, y, z));
        return this;
    }

    public ScriptActionBuilder relTeleport(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double x, double y, double z) {
        return relTeleport(new BoundingBoxCollector(minX, minY, minZ, maxX, maxY, maxZ), x, y, z);
    }

    public ScriptActionBuilder relTeleportUp(@Nonnull BoundingBoxCollector box) {
        return relTeleport(box, 0, 1, 0);
    }

    public ScriptActionBuilder relTeleportUp(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return relTeleport(new BoundingBoxCollector(minX, minY, minZ, maxX, maxY, maxZ), 0, 1, 0);
    }

    public ScriptActionBuilder relTeleportDown(@Nonnull BoundingBoxCollector box) {
        return relTeleport(box, 0, -1, 0);
    }

    public ScriptActionBuilder relTeleportDown(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return relTeleport(new BoundingBoxCollector(minX, minY, minZ, maxX, maxY, maxZ), 0, -1, 0);
    }

    // *=* Effects *=* //

    public ScriptActionBuilder sound(@Nonnull Sound sound, float pitch, double x, double y, double z) {
        script.push(new ScriptActionSound(sound, pitch, x, y, z));
        return this;
    }

    public <T> ScriptActionBuilder particle(@Nonnull Particle particle, int count, double x, double y, double z, double offsetX, double offsetY, double offsetZ, float speed, @Nullable T data) {
        script.push(new ScriptActionParticle(particle, BukkitUtils.defLocation(x, y, z), count, offsetX, offsetY, offsetZ, speed, data));
        return this;
    }

    public <T> ScriptActionBuilder particle(@Nonnull Particle particle, int count, double x, double y, double z, @Nullable T data) {
        script.push(new ScriptActionParticle(particle, BukkitUtils.defLocation(x, y, z), count, 0, 0, 0, 0, data));
        return this;
    }

    public ScriptActionBuilder particle(@Nonnull Particle particle, int count, double x, double y, double z) {
        script.push(new ScriptActionParticle(particle, BukkitUtils.defLocation(x, y, z), count, 0, 0, 0, 0, null));
        return this;
    }

    // *=* Miscellaneous *=* //

    public ScriptActionBuilder wait(int wait) {
        script.push(new ScriptActionWait(wait));
        return this;
    }

    @Nonnull
    @Override
    public Script build() {
        return script;
    }

}
