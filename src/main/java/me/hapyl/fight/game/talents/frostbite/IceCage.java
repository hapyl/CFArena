package me.hapyl.fight.game.talents.frostbite;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

import java.util.Set;

public class IceCage extends TimedGameTask implements Removable{

    private static final double[][] LOCATION_OFFSETS = {
            { 1, 0, 0 },
            { -1, 0, 0 },
            //{ 1, 1, 0 },
            //{ -1, 1, 0 },
            { 0, 0, 1 },
            { 0, 0, -1 },
            //{ 0, 1, 1 },
            //{ 0, 1, -1 },
            { 0, 2, 0 },
            { 0, -1, 0 }
    };

    private static final int PERIOD = 20;

    private final IceCageTalent talent;
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private final Location location;
    private final Set<Block> affectedBlocks;

    public IceCage(IceCageTalent talent, GamePlayer player, LivingGameEntity entity) {
        super(talent.getDuration() / PERIOD);

        this.talent = talent;
        this.player = player;
        this.entity = entity;
        this.location = CFUtils.centerLocation(entity.getLocation());
        this.affectedBlocks = Sets.newHashSet();

        createBlob();

        // Achievement
        if (player.isSelfOrTeammate(entity)) {
            Achievements.CAGE_SELF.complete(player);

            if (!player.equals(entity) && entity instanceof GamePlayer playerEntity) {
                Achievements.CAGE_SELF_OTHER.complete(playerEntity);
            }
        }

        // Fx
        player.sendMessage("&b\uD83E\uDD76 &3Your snowball hit %s!".formatted(entity.getName()));

        entity.sendMessage("&b\uD83E\uDD76 &3You got hit by %s's snowball!".formatted(player.getName()));
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
        entity.addEffect(Effects.BLINDNESS, 2, 30);
        entity.setFreezeTicks(maxTick);
        entity.triggerDebuff(player);

        runTaskTimer(0, PERIOD);
    }

    @Override
    public void remove() {
        final BlockData blockData = Material.ICE.createBlockData();

        affectedBlocks.forEach(block -> {
            block.setType(Material.AIR, false);

            // Fx
            final Location location = block.getLocation().add(0.5d, 0.5d, 0.5d);

            player.spawnWorldParticle(location, Particle.BLOCK, 5, 0.1, 0.1, 0.1, blockData);
            player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 1.25f);
        });

        affectedBlocks.clear();
    }

    @Override
    public void onLastTick() {
        remove();
        talent.iceCageMap.remove(player, this);
    }

    @Override
    public void run(int tick) {
        affectedBlocks.forEach(block -> {
            if (!(block.getBlockData() instanceof Ageable ageable)) {
                return; // should never happen
            }

            final int maxAge = ageable.getMaximumAge();
            final double stage = (double) maxAge / maxTick * tick + 0.5;

            ageable.setAge(Math.min((int) stage, maxAge));
            block.setBlockData(ageable, false);
        });

        // SFx
        player.playWorldSound(location, Sound.BLOCK_GLASS_BREAK, 0.5f + (0.5f / maxTick * tick));
    }

    private void createBlob() {
        for (final double[] offset : LOCATION_OFFSETS) {
            sendChange(location, offset);
        }

        // Fix player position
        entity.teleport(location);
    }

    private void sendChange(Location location, double[] offset) {
        final double x = offset[0];
        final double y = offset[1];
        final double z = offset[2];

        location.add(x, y, z);

        final Block block = location.getBlock();

        if (block.isEmpty()) {
            block.setType(Material.FROSTED_ICE, false);
            affectedBlocks.add(block);
        }

        location.subtract(x, y, z);
    }
}
