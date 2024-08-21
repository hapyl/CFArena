package me.hapyl.fight.game.talents.shaman;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class TotemPrison extends TickingGameTask {

    public static final int[][] OFFSETS = {
            { 1, 1 },
            { 0, 1 },
            { -1, 1 },
            { -2, 1 },
            { -2, 0 },
            { -2, -1 },
            { -2, -2 },
            { -1, -2 },
            { 0, -2 },
            { 1, -2 },
            { 1, -1 },
            { 1, 0 },
    };

    private static final BlockData BLOCK_DATA = Material.COBBLESTONE.createBlockData();

    private final TotemImprisonment talent;
    private final LivingGameEntity entity;
    private final GamePlayer player;

    private final Location location;
    private final List<Block> affectedBlocks;

    private int index = 0;

    public TotemPrison(TotemImprisonment talent, LivingGameEntity entity, GamePlayer player) {
        this.talent = talent;
        this.entity = entity;
        this.player = player;

        this.location = getLocation();
        this.affectedBlocks = Lists.newArrayList();

        runTaskTimer(1, 1);
    }

    @Override
    public void run(int tick) {
        // Have to use double checks because we need to return
        if (index != -1 && index < OFFSETS.length) {
            for (int i = 0; i < talent.buildingSpeed && index < OFFSETS.length; i++) {
                final int[] offset = OFFSETS[index];

                final int x = offset[0];
                final int z = offset[1];

                for (int y = 0; y < talent.height; y++) {
                    location.add(x, y, z);
                    create(location.getBlock(), y);
                    location.subtract(x, y, z);
                }

                index++;
            }
            return;
        }
        else if (index == OFFSETS.length) {
            // Affect entity to call debuff
            entity.triggerDebuff(player);
            index = -1;
        }

        // Tick
        if (tick >= talent.getDuration()) {
            cancel();
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
        entity.spawnWorldParticle(location, Particle.BLOCK, 20, 1, 1, 1, 1, BLOCK_DATA);
    }

    private void create(Block block, int height) {
        block.setType(entity.random.choice(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL), false);

        // Fx
        if (height == 0) {
            final Location location = block.getLocation();

            entity.playWorldSound(location, Sound.BLOCK_PISTON_EXTEND, 0.75f);
            entity.playWorldSound(location, Sound.BLOCK_NETHERITE_BLOCK_PLACE, 0.75f);
        }

        affectedBlocks.add(block);
    }

    private Location getLocation() {
        final Location location = entity.getLocation();

        return new Location(location.getWorld(), location.getBlockX(), location.getY(), location.getBlockZ());
    }
}
