package me.hapyl.fight.game.talents.shaman;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Blocks;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import java.util.Set;

public class SlimeGunk extends TickingGameTask {

    private final SlimeGunkTalent talent;
    private final GamePlayer player;
    private final Location location;
    private final Set<Block> affectedBlocks;

    public SlimeGunk(SlimeGunkTalent talent, GamePlayer player, Location location) {
        this.talent = talent;
        this.player = player;
        this.location = location;
        this.affectedBlocks = Sets.newHashSet();

        runTaskTimer(1, 1);
    }

    @Override
    public void onTaskStop() {
        Blocks.resetChanges(affectedBlocks, true);
    }

    @Override
    public void onFirstTick() {
        create();
    }

    @Override
    public void run(int tick) {
        if (tick > talent.getDuration()) {
            cancel();
            return;
        }

        if (!modulo(talent.period)) {
            return;
        }

        // Affect
        Collect.nearbyEntities(location, talent.diameter / 2.0d).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.damageNoKnockback(1, player, DamageCause.POISON);
        });

        // Fx
        for (Block block : affectedBlocks) {
            player.spawnWorldParticle(block.getLocation(), Particle.BLOCK, 5, 0.449d, 0.1d, 0.449d, talent.blockData);
        }

        player.playWorldSound(location, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.75f);
    }

    private void create() {
        final double diameter = talent.diameter;
        final double diameter1 = diameter - 1;

        final int offset = (int) diameter / 2;

        location.subtract(offset, 0, offset);

        for (int x = 0; x < diameter; x++) {
            for (int z = 0; z < diameter; z++) {
                // Skip corners
                if ((x == 0 && z == 0) || (x == diameter1 && z == 0) || (x == 0 && z == diameter1) || (x == diameter1 && z == diameter1)) {
                    continue;
                }

                location.add(x, 0, z);

                final Block block = location.getBlock();

                if (block.isEmpty()) {
                    affectedBlocks.add(block);

                    // Fx
                    player.playWorldSound(location, Sound.BLOCK_VINE_PLACE, player.random.nextFloat(0.0f, 0.75f));
                }

                location.subtract(x, 0, z);
            }
        }

        Blocks.sendChanges(affectedBlocks, Material.VINE);

        // Return location to normal and center for further checks
        location.add(offset + 0.5d, 0.5d, offset + 0.5d);
    }
}
