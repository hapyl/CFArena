package me.hapyl.fight.game.talents.archive.shaman;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;

public class TotemPrison extends TickingGameTask {

    private final TotemImprisonment talent;
    private final LivingGameEntity entity;
    private final Location location;
    private final List<Block> affectedBlocks;

    public TotemPrison(TotemImprisonment talent, LivingGameEntity entity) {
        this.talent = talent;
        this.entity = entity;
        this.location = getLocation();
        this.affectedBlocks = Lists.newArrayList();

        create();
        runTaskTimer(1, 1);
    }

    @Override
    public void run(int tick) {
        // Animation
        final int castingTime = affectedBlocks.size() / talent.height;

        if (tick < castingTime) {
            for (int i = 0; i < talent.height; i++) {
                next(tick * talent.height + i);
            }
        }
        // Tick
        else {
            if (tick - castingTime > talent.getDuration()) {
                cancel();
            }
        }
    }

    @Override
    public void onTaskStop() {
        affectedBlocks.forEach(block -> {
            block.setType(Material.AIR, false);
        });

        affectedBlocks.clear();

        // Fx
        entity.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        entity.spawnWorldParticle(location, Particle.CRIT, 20, 1, 1, 1, 1);
    }

    private void next(int index) {
        if (index >= affectedBlocks.size()) {
            return;
        }

        final Block block = affectedBlocks.get(index);

        block.setType(entity.random.choice(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL), false);

        // Fx
        if (index % talent.height == 0) {
            final Location location = block.getLocation();

            entity.playWorldSound(location, Sound.BLOCK_PISTON_EXTEND, 0.75f);
            entity.playWorldSound(location, Sound.BLOCK_NETHERITE_BLOCK_PLACE, 0.75f);
        }
    }

    private void create() {
        location.subtract(2, 0, 2);

        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                if (x != 0 && z != 0 && x != 3 && z != 3) {
                    continue;
                }

                location.add(x, 0, z);

                final Block relative = location.getBlock();

                for (int i = 0; i < talent.height; i++) {
                    final Block block = relative.getRelative(BlockFace.UP, i);

                    if (block.isEmpty()) {
                        affectedBlocks.add(block);
                    }
                }

                location.subtract(x, 0, z);
            }
        }

        location.add(2, 0, 2);
    }

    private Location getLocation() {
        final Location location = entity.getLocation();

        return new Location(location.getWorld(), location.getBlockX(), location.getY(), location.getBlockZ());
    }
}
