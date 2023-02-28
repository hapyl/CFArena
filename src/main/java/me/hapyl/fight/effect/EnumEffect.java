package me.hapyl.fight.effect;

import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO -> This is a temporary class, will be replaced by cosmetics later.
 */
public class EnumEffect implements Listener {

    public static void tempDisplayGroundPunch(Player player) {
        final Location location = player.getLocation().clone().subtract(1, 1, 1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    continue;
                }
                location.add(i, 0, j);
                propelGround(location);
                location.subtract(i, 0, j);
            }
        }

        new GameTask() {
            @Override
            public void run() {
                location.subtract(1.0d, 0.0d, 1.0d).add(0.0d, 0.35d, 0.0d);
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (((i == 0 || i == 4) && j > 0 && j < 4) || j % 4 == 0) {
                            location.add(i, 0, j);
                            propelGround(location);
                            location.subtract(i, 0, j);
                        }
                    }
                }
            }
        }.runTaskLater(2);

        new GameTask() {
            @Override
            public void run() {
                location.subtract(1.0d, 0.0d, 1.0d).add(0.0d, 0.35f, 0.0d);
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 7; j++) {
                        if ((i == 0 || i == 6) && (j == 0 || j == 6)) {
                            continue;
                        }
                        if (i == 0 || i == 6 || j % 6 == 0) {
                            location.add(i, 0, j);
                            propelGround(location);
                            location.subtract(i, 0, j);
                        }
                    }
                }
            }
        }.runTaskLater(4);


    }

    private static void propelGround(Location location) {
        if (!location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            return;
        }

        final Material block = location.getBlock().getType().isAir() ? Material.COBBLESTONE : location.getBlock().getType();
        final BlockData blockData = block.createBlockData();
        if (location.getWorld() == null) {
            return;
        }

        final FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location.clone().add(0.0d, 1.01d, 0.0d), blockData);
        fallingBlock.addScoreboardTag("Cosmetic");
        fallingBlock.setHurtEntities(false);
        fallingBlock.setDropItem(false);
        fallingBlock.setVelocity(new Vector(
                0.025f * ThreadLocalRandom.current().nextFloat(),
                0.5d,
                0.025f * ThreadLocalRandom.current().nextFloat()
        ));
        final SoundGroup soundGroup = blockData.getSoundGroup();
        location.getWorld().playSound(
                location,
                soundGroup.getBreakSound(),
                soundGroup.getVolume() * 2,
                soundGroup.getPitch() + Math.max(0.0f, Math.min((0.1f * ThreadLocalRandom.current().nextFloat()), 2.0f))
        );
    }

    @EventHandler()
    public void handleEntityChangeBlockEvent(EntityChangeBlockEvent ev) {
        // Auto-Generated
        final Entity entity = ev.getEntity();
        if (entity instanceof FallingBlock && entity.getScoreboardTags().contains("Cosmetic")) {
            ev.setCancelled(true);
            entity.remove();
        }
    }

}
