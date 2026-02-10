package me.hapyl.fight.game.talents.moonwalker;

import me.hapyl.eterna.module.math.geometry.Draw;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;

public class BlockDataDraw extends Draw {

    private final BlockData blockData;

    public BlockDataDraw(@Nonnull Particle particle, @Nonnull BlockData blockData) {
        super(particle);

        if (particle.getDataType() != BlockData.class) {
            throw new IllegalArgumentException(particle + " cannot have block data");
        }

        this.blockData = blockData;
    }

    @Override
    public void draw(Location location) {
        final World world = location.getWorld();

        if (world != null) {
            world.spawnParticle(this.getParticle(), location, 0, 0, 0, 0, blockData);
        }

    }
}
