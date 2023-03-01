package me.hapyl.fight.game.cosmetic.contrail;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockContrailCosmetic extends ContrailCosmetic {

    private final List<Material> materials;
    private int stay;

    public BlockContrailCosmetic(String name, String description, long cost, Rarity rarity) {
        super(name, description, cost, rarity);

        this.materials = Lists.newArrayList();
        this.stay = 20;
    }

    public void addMaterials(Material... materials) {
        for (Material material : materials) {
            if (!material.isBlock()) {
                throw new IllegalArgumentException("Material must be a block!");
            }
        }

        this.materials.addAll(Lists.newArrayList(materials));
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public int getStay() {
        return stay;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    @Override
    public void onMove(Display display) {
        final Location location = display.getLocation().subtract(0.0d, 1.0d, 0.0);
        final Block block = location.getBlock();

        if (!block.getType().isOccluding()) {
            return;
        }

        // Get random material
        final Material material = randomElement();

        for (Player player : display.getPlayersWhoCanSeeContrail()) {
            player.sendBlockChange(location, material.createBlockData());
        }

        GameTask.runLater(() -> {
            block.getState().update(true, false);
        }, getStay()).runTaskAtCancel();
    }

    private Material randomElement() {
        if (materials.size() == 1) {
            return materials.get(0);
        }

        return CollectionUtils.randomElement(materials, materials.get(0));
    }
}
