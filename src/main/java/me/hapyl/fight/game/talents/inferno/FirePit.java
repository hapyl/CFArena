package me.hapyl.fight.game.talents.inferno;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class FirePit implements Removable {

    public static final BlockData AIR = Material.AIR.createBlockData();
    public static final Material FIRE_MATERIAL = Material.SOUL_FIRE;

    private final FirePitTalent talent;
    private final GamePlayer player;

    private final List<Location> locations;
    private final List<Location> fireLocations;

    public FirePit(@Nonnull FirePitTalent talent, @Nonnull GamePlayer player, @Nonnull Location centre) {
        this.talent = talent;
        this.player = player;
        this.locations = createLocations(centre);
        this.fireLocations = locations.stream().map(location -> location.clone().add(0, 1, 0)).collect(Collectors.toList());
    }

    @Nonnull
    public List<Location> locations() {
        return locations;
    }

    @Nonnull
    public List<Location> fireLocations() {
        return fireLocations;
    }

    public void transform(@Nonnull BlockData data) {
        this.locations.forEach(location -> {
            final Block block = location.getBlock();

            CFUtils.globalBlockChange(block.getLocation(), data);
        });
    }

    public void lightTheFire() {
        fireLocations.forEach(location -> {
            final Block block = location.getBlock();

            if (block.getType() == Material.AIR) {
                block.setType(FIRE_MATERIAL, false);
            }
        });
    }

    @Override
    public void remove() {
        // Update fx blocks
        CFUtils.clearCollectionAnd(locations, location -> location.getBlock().getState().update(true, false));

        final Location centre = fireLocations.getFirst();

        CFUtils.clearCollectionAnd(
                fireLocations, location -> {
                    final Block block = location.getBlock();

                    if (block.getType() == FIRE_MATERIAL) {
                        block.setType(Material.AIR, false);
                    }
                }
        );

        // Fx
        player.playWorldSound(centre, Sound.BLOCK_FIRE_EXTINGUISH, 0.75f);
    }

    public boolean isInFire(@Nonnull LivingGameEntity entity) {
        for (Location location : fireLocations) {
            if (isInFireBlock(entity, location.getBlock())) {
                return true;
            }
        }

        return false;
    }

    private List<Location> createLocations(@Nonnull Location centre) {
        final List<Location> locations = Lists.newArrayList();

        for (int[] offset : talent.firePitsOffsets) {
            locations.add(LocationHelper.anchor(centre.clone().add(offset[0], 0, offset[1])).subtract(0, 1, 0));
        }

        return locations;
    }

    public static boolean isInFireBlock(@Nonnull LivingGameEntity entity, @Nonnull Block block) {
        final Material type = block.getType();

        if (type != Material.FIRE && type != Material.SOUL_FIRE) {
            return false;
        }

        final BoundingBox boundingBox = entity.boundingBox();

        return boundingBox.overlaps(block.getBoundingBox().shift(0.0d, 0.9d, 0.0d));
    }
}
