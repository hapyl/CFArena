package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class GroundPunchCosmetic extends Cosmetic implements Listener {
    public GroundPunchCosmetic(@Nonnull Key key) {
        super(key, "Ground Punch", Type.KILL);

        setDescription("""
                Me smash ground!
                """
        );

        setRarity(Rarity.RARE);
        setIcon(Material.COARSE_DIRT);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        playAnimation(display.getLocation(), 2);
    }

    public void playAnimation(Location displayLocation, int delay) {
        final Location location = displayLocation.clone().subtract(1, 1, 1);

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
        }.runTaskLater(delay);

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
        }.runTaskLater(delay * 2L);
    }

    private void propelGround(Location location) {
        if (!location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            return;
        }

        final Material blockType = location.getBlock().getType().isAir() ? Material.COBBLESTONE : location.getBlock().getType();
        final BlockData blockData = blockType.createBlockData();
        if (location.getWorld() == null) {
            return;
        }

        final FallingBlock block = location.getWorld().spawnFallingBlock(location.clone().add(0.0d, 1.01d, 0.0d), blockData);
        block.addScoreboardTag("Cosmetic");
        block.setHurtEntities(false);
        block.setDropItem(false);

        block.setVelocity(new Vector(
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
        final Entity entity = ev.getEntity();
        if (entity instanceof FallingBlock && entity.getScoreboardTags().contains("Cosmetic")) {
            ev.setCancelled(true);
            entity.remove();
        }
    }

}
