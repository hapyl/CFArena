package me.hapyl.fight.game.cosmetic.contrail;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockContrailCosmetic extends ContrailCosmetic {

    private final List<Material> materials;
    private int stay;

    public BlockContrailCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull Rarity rarity) {
        super(key, name, description, rarity, BLOCK);

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

    public boolean isValidBlock(Block block) {
        return block.getType().isOccluding();
    }

    public boolean canUseContrail(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        // Not in game
        if (gamePlayer == null) {
            return true;
        }

        return gamePlayer.isValidForCosmetics();
    }

    public boolean isNotValid(Player player, Block block) {
        return !canUseContrail(player) || !isValidBlock(block);
    }

    @Override
    public void onMove(@Nonnull Display display, int tick) {
        final Location location = display.getLocation().subtract(0.0d, 1.0d, 0.0);
        final Block block = location.getBlock();
        final Player player = display.getPlayer();

        if (isNotValid(player, block)) {
            return;
        }

        // Get random material
        final Material material = randomElement();

        for (Player canSee : display.getPlayersWhoCanSeeContrail()) {
            canSee.sendBlockChange(location, material.createBlockData());
        }

        GameTask.runLater(() -> block.getState().update(true, false), getStay()).runTaskAtCancel();
    }

    private Material randomElement() {
        if (materials.size() == 1) {
            return materials.getFirst();
        }

        return CollectionUtils.randomElement(materials, materials.getFirst());
    }
}
