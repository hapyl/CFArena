package me.hapyl.fight.game.talents.archive.frostbite;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class IceCage implements Removable {

    private static final BlockData[] BLOCK_DATA = {
            Material.ICE.createBlockData(),
            Material.PACKED_ICE.createBlockData(),
            Material.BLUE_ICE.createBlockData(),
    };

    private static final double[][] LOCATION_OFFSETS = {
            { 1, 0, 0 },
            { -1, 0, 0 },
            { 1, 1, 0 },
            { -1, 1, 0 },
            { 0, 0, 1 },
            { 0, 0, -1 },
            { 0, 1, 1 },
            { 0, 1, -1 },
            { 0, 2, 0 }
    };

    private final GamePlayer player;
    private final GamePlayer entity;
    private final Set<Block> affectedBlocks;

    public IceCage(GamePlayer player, GamePlayer entity) {
        this.player = player;
        this.entity = entity;
        this.affectedBlocks = Sets.newHashSet();

        createBlob();

        // Achievement
        if (player.equals(entity)) {
            Achievements.CAGE_SELF.complete(player);
        }

        // Fx
        player.sendMessage("&b\uD83E\uDD76 &3Your snowball hit %s!", entity.getName());

        entity.sendMessage("&b\uD83E\uDD76 &3You got hit by %s's snowball! &6&lCLICK &3the ice to remove it!", player.getName());
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
        entity.addPotionEffect(PotionEffectType.BLINDNESS, 30, 2);
        entity.setFreezeTicks(100);
    }

    @Override
    public void remove() {
        affectedBlocks.forEach(block -> block.getState().update(true, false));
        affectedBlocks.clear();
    }

    private void createBlob() {
        final Location location = entity.getLocation();

        for (int i = 0; i < LOCATION_OFFSETS.length; i++) {
            final double[] offset = LOCATION_OFFSETS[i];
            sendChange(location, offset, BLOCK_DATA[i % BLOCK_DATA.length]);
        }

        // Fix player position
        entity.teleport(CFUtils.centerLocation(location));
    }

    private void sendChange(Location location, double[] offset, BlockData data) {
        final double x = offset[0];
        final double y = offset[1];
        final double z = offset[2];

        location.add(x, y, z);
        entity.getPlayer().sendBlockChange(location, data);
        location.subtract(x, y, z);
    }
}
