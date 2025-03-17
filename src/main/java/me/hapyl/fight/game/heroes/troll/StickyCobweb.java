package me.hapyl.fight.game.heroes.troll;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

public class StickyCobweb extends TickingGameTask {

    private static final DisplayData DISPLAY = BDEngine.parse("/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cobweb\",Properties:{}},transformation:[1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f]}]}");

    private final TrollData data;
    private final GamePlayer player;
    private final Map<Block, DisplayEntity> blocks;

    public StickyCobweb(@Nonnull TrollData data, @Nonnull GamePlayer player) {
        this.data = data;
        this.player = player;
        this.blocks = Maps.newHashMap();

        final Location location = player.getLocation().subtract(2, 0, 2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == 0 || i == 4) && (j == 0 || j == 4)) {
                    continue;
                }

                location.add(i, 0, j);

                final Block block = location.getBlock();

                if (block.isEmpty()) {
                    block.setType(Material.TRIPWIRE, false);
                    blocks.put(block, DISPLAY.spawn(block.getLocation()));
                }

                location.subtract(i, 0, j);
            }
        }

        runTaskTimer(0, 1);
    }

    @Override
    public void run(int tick) {
        if (blocks.isEmpty()) {
            data.remove();
            return;
        }

        final Iterator<Map.Entry<Block, DisplayEntity>> iterator = blocks.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<Block, DisplayEntity> next = iterator.next();
            final Block block = next.getKey();
            final BoundingBox boundingBox = block.getBoundingBox().shift(0, 1, 0);
            final DisplayEntity display = next.getValue();

            // Collision
            final LivingGameEntity target = Collect.nearestEntity(
                    block.getLocation(), 2.0d, entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return false;
                        }

                        return !entity.getAttributes().hasTemper(Temper.STICKY) && entity.boundingBox().overlaps(boundingBox);
                    }
            );

            // Affect
            if (target != null) {
                iterator.remove();

                block.setType(Material.AIR, false);
                display.remove();

                final EntityAttributes attributes = target.getAttributes();
                attributes.decreaseTemporary(Temper.STICKY, AttributeType.SPEED, attributes.get(AttributeType.SPEED) * 0.3d, 60, player);
            }
        }
    }

    public void clear(@Nonnull Block block) {
        final DisplayEntity display = blocks.remove(block);

        if (display == null) {
            return;
        }

        block.setType(Material.AIR, false);
        display.remove();

        // Fx
        final Location location = block.getLocation().add(0.5d, 0.5d, 0.5d);

        player.spawnWorldParticle(location, Particle.POOF, 5, 0.3d, 0.2d, 0.3d, 0.03f);
        player.playWorldSound(location, Sound.BLOCK_WOOL_PLACE, 0.0f);
    }

    public void remove() {
        blocks.forEach((block, display) -> {
            block.setType(Material.AIR, false);
            display.remove();
        });

        cancel();
    }
}

