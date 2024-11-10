package me.hapyl.fight.game.heroes.echo;

import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockBrightness {

    private static final Map<Material, Float> BLOCK_BRIGHTNESS_MAP;

    private static final Palette[] PALETTE = {
            Palette.of(Material.GRAY_CONCRETE, Material.DEEPSLATE_TILE_STAIRS, Material.DEEPSLATE_TILE_SLAB),
            Palette.of(Material.GRAY_WOOL, Material.DEEPSLATE_TILE_STAIRS, Material.DEEPSLATE_TILE_SLAB),
            Palette.of(Material.GRAY_CONCRETE_POWDER, Material.POLISHED_TUFF_STAIRS, Material.POLISHED_TUFF_SLAB),
            Palette.of(Material.CYAN_TERRACOTTA, Material.POLISHED_TUFF_STAIRS, Material.POLISHED_TUFF_SLAB),
            Palette.of(Material.STONE, Material.STONE_STAIRS, Material.STONE_SLAB),
            Palette.of(Material.ANDESITE, Material.STONE_STAIRS, Material.STONE_SLAB),
            Palette.of(Material.LIGHT_GRAY_WOOL, Material.ANDESITE_STAIRS, Material.ANDESITE_SLAB),
            Palette.of(Material.LIGHT_GRAY_CONCRETE_POWDER, Material.ANDESITE_STAIRS, Material.ANDESITE_SLAB),
    };

    private static final BlockData DEFAULT_BLOCK;
    private static final BlockData FENCE_BLOCK;
    private static final BlockData WALL_BLOCK;
    private static final BlockData GLASS_BLOCK;
    private static final BlockData CARPET_BLOCK;
    private static final BlockData HEAD_BLOCK;
    private static final BlockData TRAPDOOR_BLOCK;
    private static final BlockData DOOR_BLOCK;

    static {
        BLOCK_BRIGHTNESS_MAP = Maps.newHashMap();

        DEFAULT_BLOCK = Material.STONE.createBlockData();
        FENCE_BLOCK = Material.ANDESITE_WALL.createBlockData();
        WALL_BLOCK = Material.TUFF_WALL.createBlockData();
        GLASS_BLOCK = Material.BLACK_STAINED_GLASS.createBlockData();
        CARPET_BLOCK = Material.GRAY_CARPET.createBlockData();
        HEAD_BLOCK = Material.WITHER_SKELETON_SKULL.createBlockData();
        TRAPDOOR_BLOCK = Material.IRON_TRAPDOOR.createBlockData();
        DOOR_BLOCK = Material.IRON_DOOR.createBlockData();

        for (Material material : Material.values()) {
            if (!material.isBlock()) {
                continue;
            }

            final BlockData data = material.createBlockData();
            final Color color = data.getMapColor();
            final int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

            BLOCK_BRIGHTNESS_MAP.put(material, gray / 255f);
        }
    }

    public static float getBrightness(@Nonnull Material material) {
        return BLOCK_BRIGHTNESS_MAP.getOrDefault(material, -1.0f);
    }

    @Nonnull
    public static BlockData getBlock(@Nonnull Block block) {
        final BlockData blockData = block.getBlockData();
        final Material material = block.getType();

        // Player head heads
        // Changing the rotation is pain so whatever good enough for me
        if (material == Material.PLAYER_HEAD) {
            return HEAD_BLOCK;
        }

        // Carpets
        if (Tag.WOOL_CARPETS.isTagged(material)) {
            return CARPET_BLOCK;
        }

        // Glass
        switch (material) {
            case GLASS, GLASS_PANE, ORANGE_STAINED_GLASS, RED_STAINED_GLASS, BLACK_STAINED_GLASS,
                 LIME_STAINED_GLASS, MAGENTA_STAINED_GLASS, MAGENTA_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS, BLUE_STAINED_GLASS,
                 GRAY_STAINED_GLASS, WHITE_STAINED_GLASS, YELLOW_STAINED_GLASS, BROWN_STAINED_GLASS, GREEN_STAINED_GLASS,
                 CYAN_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, PINK_STAINED_GLASS, PURPLE_STAINED_GLASS, TINTED_GLASS -> {
                return GLASS_BLOCK;
            }
        }

        // Grass
        switch (material) {
            case SHORT_GRASS, TALL_GRASS, SEAGRASS, FERN, LARGE_FERN, TALL_SEAGRASS -> {
                final Waterlogged echo = (Waterlogged) Material.DEAD_BRAIN_CORAL.createBlockData();
                echo.setWaterlogged(false);

                return echo;
            }
        }

        // Flowers
        if (Tag.FLOWERS.isTagged(material)) {
            final Waterlogged echo = (Waterlogged) Material.DEAD_TUBE_CORAL.createBlockData();
            echo.setWaterlogged(false);

            return echo;
        }

        final float brightness = getBrightness(material);

        if (brightness == -1.0f) {
            return DEFAULT_BLOCK; // Default to stone ig ¯\_(ツ)_/¯
        }

        final Palette palette = PALETTE[(int) Math.floor(brightness * (PALETTE.length - 1))];

        // Copy stairs and slab data
        return switch (blockData) {
            case Stairs real -> {
                final BlockData echo = palette.stair.clone();
                real.copyTo(echo);

                yield echo;
            }
            case Slab real -> {
                final BlockData echo = palette.slab.clone();
                real.copyTo(echo);

                yield echo;
            }
            // Mimic fences and walls
            case Fence real -> {
                final Wall echo = (Wall) FENCE_BLOCK.clone();
                real.getFaces().forEach(face -> echo.setHeight(face, Wall.Height.TALL));

                yield echo;
            }
            case Wall real -> {
                final BlockData echo = WALL_BLOCK.clone();
                real.copyTo(echo);

                yield echo;
            }
            case TrapDoor real -> {
                final BlockData echo = TRAPDOOR_BLOCK.clone();
                real.copyTo(echo);

                yield echo;
            }
            case Door real -> {
                final BlockData echo = DOOR_BLOCK.clone();
                real.copyTo(echo);

                yield echo;
            }
            default -> palette.block;
        };
    }

    private static class Palette {

        private final BlockData block;
        private final BlockData stair;
        private final BlockData slab;

        Palette(Material block, Material stair, Material slab) {
            this.block = block.createBlockData();
            this.stair = stair.createBlockData();
            this.slab = slab.createBlockData();
        }

        public static Palette of(Material block, Material stair, Material slab) {
            return new Palette(block, stair, slab);
        }

    }

}
