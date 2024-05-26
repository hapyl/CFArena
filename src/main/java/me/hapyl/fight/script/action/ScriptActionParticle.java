package me.hapyl.fight.script.action;

import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.ScriptRunner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class ScriptActionParticle implements ScriptAction {

    private final Particle particle;
    private final Location location;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final float speed;
    private final Object data;

    <T> ScriptActionParticle(@Nonnull Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, float speed, T data) {
        this.particle = particle;
        this.location = location;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.data = data;
    }

    @Override
    public void execute(@Nonnull ScriptRunner runner) {
        final World world = location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, data);
    }

    @Override
    public String toString() {
        return "%sx%s".formatted(particle.name(), count);
    }
}
