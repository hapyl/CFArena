package me.hapyl.fight.util;

import me.hapyl.fight.game.Debug;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface MaterialCooldown {

    @Nonnull
    Material getCooldownMaterial();

    int getCooldown();

    default void startCooldown(@Nonnull Player player) {
        player.setCooldown(getCooldownMaterial0(), getCooldown0());
    }

    default boolean hasCooldown(@Nonnull Player player) {
        return player.hasCooldown(getCooldownMaterial0());
    }

    default int getCooldown(@Nonnull Player player) {
        return player.getCooldown(getCooldownMaterial0());
    }

    private Material getCooldownMaterial0() {
        final Material material = getCooldownMaterial();

        if (!material.isItem()) {
            Debug.severe("Cooldown material is not an item in " + getClass().getSimpleName() + "!");
            return Material.BEDROCK;
        }

        return material;
    }

    private int getCooldown0() {
        return Math.max(0, getCooldown());
    }

}
