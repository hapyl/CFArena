package me.hapyl.fight.script.scripts;

import me.hapyl.fight.registry.Key;
import me.hapyl.fight.script.Script;
import me.hapyl.fight.script.action.ScriptActionBuilder;
import me.hapyl.fight.util.BlockDataMaker;
import me.hapyl.fight.util.BlockVibration;
import me.hapyl.eterna.module.math.Cuboid;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Vibration;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.TrapDoor;

import javax.annotation.Nonnull;

public class WineryLiftScript extends Script {

    private final Cuboid liftCuboid = new Cuboid(4983, 43, 28, 4985, 47, 30);
    private final Vibration vibration = new BlockVibration(
            4982.5, 61.5, 10.5,
            4977.5, 69.5, 10.5,
            5
    );

    public WineryLiftScript(@Nonnull Key key) {
        super(key);

        final ScriptActionBuilder builder = builder();

        // Vibration
        builder
                .particle(Particle.VIBRATION, 1, 4982.5, 61.25, 10.5, vibration)
                .wait(40);

        // Move Up
        for (int i = 1; i < 6; i++) {
            final int currentY = 60 + i;

            builder.relTeleport(
                    4977, currentY, 11,
                    4979, 63 + i, 13,
                    0, 1.5, 0
            );

            builder.fill(
                    4977, currentY - 1, 11,
                    4979, 60, 13,
                    Material.AIR
            );

            builder.clone(liftCuboid, 4977, currentY, 11);
            builder.sound(Sound.BLOCK_CHAIN_BREAK, 0.3f, 4978, 65, 12);

            // Don't wait at the last iterations
            if (i != 5) {
                builder.wait(20);
            }
        }

        // Close trapdoors
        builder.fill(4980, 65, 11, 4980, 65, 13,
                BlockDataMaker.of(Material.SPRUCE_TRAPDOOR, TrapDoor.class, data -> {
                    data.setFacing(BlockFace.WEST);
                    data.setHalf(Bisected.Half.TOP);
                    data.setOpen(false);
                })
        );

        builder.sound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 0.0f, 4978, 65, 12);
        builder.wait(60);

        // Open trapdoors
        builder.fill(4980, 65, 11, 4980, 65, 13,
                BlockDataMaker.of(Material.SPRUCE_TRAPDOOR, TrapDoor.class, data -> {
                    data.setFacing(BlockFace.WEST);
                    data.setHalf(Bisected.Half.TOP);
                    data.setOpen(true);
                })
        );

        builder.sound(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.0f, 4978, 65, 12);
        builder.wait(10);

        // Go down
        for (int i = 4; i >= 0; i--) {
            builder.clone(liftCuboid, 4977, 60 + i, 11);
            builder.sound(Sound.BLOCK_CHAIN_BREAK, 0.4f, 4978, 65, 12);
            builder.wait(20);
        }

    }

}
