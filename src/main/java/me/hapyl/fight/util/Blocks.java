package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.CF;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// static util for blocks
public final class Blocks {

    private Blocks() {
    }

    /**
     * Returns true if the given block is <code>valid</code>.
     *
     * @param block - Block to check.
     * @return true if the block is valid, false otherwise.
     * @see #isValid(Block)
     */
    public static boolean isValid(@Nullable Block block) {
        if (block == null) {
            return false;
        }

        return isValid(block.getType());
    }

    /**
     * Returns true if the given material is <code>valid</code>.
     *
     * @param material - Material to check.
     * @return true if the material is valid, false otherwise.
     */
    public static boolean isValid(@Nonnull Material material) {
        return switch (material) {
            case AIR, CAVE_AIR, VOID_AIR, BARRIER -> false;
            default -> true;
        };
    }

    /**
     * Checks if the given block is one of the given types.
     *
     * @param block - Block to check.
     * @param types - Types.
     * @return true if the given block is one of the given types, false otherwise.
     */
    public static boolean checkType(@Nullable Block block, @Nonnull Material... types) {
        if (block == null) {
            return false;
        }

        for (Material type : types) {
            if (block.getType() == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given block is <b>not</b> one of the given types.
     *
     * @param block - Block to check.
     * @param types - Types.
     * @return true if the given block is <b>not</b> one of the given types.
     */
    public static boolean checkNotType(@Nullable Block block, @Nonnull Material... types) {
        if (block == null) {
            return false;
        }

        final Material blockType = block.getType();

        for (Material type : types) {
            if (blockType == type) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sends the given block changes to all {@link me.hapyl.fight.game.entity.GamePlayer}s with the given material.
     *
     * @param blocks   - Blocks to send change for.
     * @param material - Material.
     */
    public static void sendChanges(@Nonnull Collection<Block> blocks, @Nonnull Material material) {
        final BlockData data = material.createBlockData();

        CF.getPlayers().forEach(player -> {
            blocks.forEach(block -> {
                player.getEntity().sendBlockChange(block.getLocation(), data);
            });
        });
    }

    /**
     * Resets the state of the given blocks.
     *
     * @param blocks          - Block to reset state for.
     * @param clearCollection - If true, the given collection will be cleared.
     */
    public static void resetChanges(@Nonnull Collection<Block> blocks, boolean clearCollection) {
        blocks.forEach(block -> {
            block.getState().update(true, false);
        });

        if (clearCollection) {
            blocks.clear();
        }
    }

    @Nonnull
    public static Map<Material, Integer> count(@Nonnull Collection<Block> blocks) {
        final Map<Material, Integer> count = new TreeMap<>();
        blocks.forEach(block -> count.compute(block.getType(), Compute.intAdd()));

        return count;
    }
}
