package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A static mapped {@link Material} helper.
 */
public final class Materials {

    private static final Map<MaterialCategory, List<Material>> mapped;

    // Init materials
    static {
        mapped = Maps.newHashMap();

        for (Material material : Material.values()) {
            final MaterialCategory category = MaterialCategory.of(material);

            if (category != null) {
                compute(category, material);
            }
        }
    }

    private Materials() {
    }

    /**
     * Gets a copy of a {@link List} by the given {@link MaterialCategory}.
     * <p>
     * The list is guaranteed to only include the matching materials.
     *
     * @param category - Category.
     * @return a copy list with materials of the given category.
     */
    @Nonnull
    public static List<Material> getMaterials(@Nonnull MaterialCategory category) {
        return Lists.newArrayList(mapped.getOrDefault(category, Collections.emptyList()));
    }

    /**
     * Performs an iteration over a given {@link MaterialCategory}.
     * <p>
     * Each item is guaranteed to be within the category.
     * <p>
     * Iterations are performed on the actual list, which is sorted the same way {@link Material} is.
     *
     * @param category - Category.
     * @param consumer - Consumer.
     */
    public static void iterate(@Nonnull MaterialCategory category, @Nonnull Consumer<Material> consumer) {
        final List<Material> materials = mapped.get(category);

        if (materials != null) {
            materials.forEach(consumer);
        }
    }

    public static void setItemCooldowns(int cooldown) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        iterate(MaterialCategory.ITEM, material -> {
            players.forEach(player -> {
                player.setCooldown(material, cooldown);
            });
        });
    }

    public static void setCooldown(@Nonnull Material material, int cd) {
        Validate.isTrue(material.isItem(), "Material must be an item!");

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setCooldown(material, cd);
        });
    }

    private static void compute(MaterialCategory category, Material material) {
        mapped.compute(category, Compute.listAdd(material));
    }


}
