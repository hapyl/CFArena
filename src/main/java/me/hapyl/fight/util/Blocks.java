package me.hapyl.fight.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// static util to validate blocks
public final class Blocks {

    private Blocks() {
    }

    public static boolean isValid(@Nullable Block block) {
        if (block == null) {
            return false;
        }

        return isValid(block.getType());
    }

    // default to air and barrier checks
    public static boolean isValid(@Nonnull Material material) {
        return switch (material) {
            case AIR, CAVE_AIR, VOID_AIR, BARRIER -> false;
            default -> true;
        };
    }

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

    public static boolean checkNotType(@Nullable Block block, @Nonnull Material... types) {
        if (block == null) {
            return false;
        }

        return !checkType(block, types);
    }

}
