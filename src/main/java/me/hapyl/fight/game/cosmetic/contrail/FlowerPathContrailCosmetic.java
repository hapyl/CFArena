package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FlowerPathContrailCosmetic extends BlockContrailCosmetic {

    private final Material[] flowers = new Material[] {
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
    };

    public FlowerPathContrailCosmetic(@Nonnull Key key) {
        super(key, "Flower Path", "Leave a string of flowers behind you. So pretty! &d(✿◡‿◡)", Rarity.LEGENDARY);

        setIcon(Material.ROSE_BUSH);
        setStay(10);
    }

    @Override
    public void onMove(@Nonnull Display display, int tick) {
        final Location location = display.getLocation();
        final Player player = display.getPlayer();
        final Block block = location.getBlock();
        final Block blockDown = location.getBlock().getRelative(BlockFace.DOWN);

        if (isNotValid(player, blockDown) || !block.getType().isAir()) {
            return;
        }

        display.getPlayersWhoCanSeeContrail().forEach(canSee -> canSee.sendBlockChange(location, randomFlower()));
        GameTask.runLater(() -> block.getState().update(true, false), getStay()).runTaskAtCancel();
    }

    private BlockData randomFlower() {
        final Material material = CollectionUtils.randomElement(flowers, flowers[0]);
        final BlockData blockData = material.createBlockData();

        if (material == Material.LILAC || material == Material.ROSE_BUSH || material == Material.PEONY || material == Material.SUNFLOWER) {
            if (blockData instanceof Bisected bisected) {
                bisected.setHalf(Bisected.Half.TOP);
            }
        }

        return blockData;
    }
}
