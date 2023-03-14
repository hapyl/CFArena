package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class FlowerPathContrail extends BlockContrailCosmetic {

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

    public FlowerPathContrail() {
        super("Flower Path", "Leaves a path of flowers behind you. So pretty! &d(✿◡‿◡)", 5000, Rarity.LEGENDARY);

        setIcon(Material.ROSE_BUSH);
        setStay(10);
    }

    @Override
    public void onMove(Display display) {
        final Location location = display.getLocation();
        final Player player = display.getPlayer();
        final Block block = location.getBlock();
        final Block blockDown = location.getBlock().getRelative(BlockFace.DOWN);

        if (!isValid(player, blockDown) || !block.getType().isAir()) {
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
