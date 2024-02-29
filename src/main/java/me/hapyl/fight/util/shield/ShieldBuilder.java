package me.hapyl.fight.util.shield;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Builder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import javax.annotation.Nonnull;

public class ShieldBuilder implements Builder<ItemStack> {

    private final ItemStack stack;
    private final BlockStateMeta meta;
    private final Banner banner;

    public ShieldBuilder() {
        this(DyeColor.WHITE);
    }

    public ShieldBuilder(@Nonnull DyeColor baseColor) {
        stack = new ItemBuilder(Material.SHIELD).setName("&dEnergy Shield").setUnbreakable().asIcon();
        meta = (BlockStateMeta) stack.getItemMeta();

        if (meta == null) {
            throw new NullPointerException("meta null");
        }

        banner = (Banner) meta.getBlockState();
        banner.setBaseColor(baseColor);
    }

    public ShieldBuilder with(@Nonnull PatternType type) {
        return with(DyeColor.BLACK, type);
    }

    public ShieldBuilder with(@Nonnull DyeColor color, PatternType type) {
        return with(new Pattern(color, type));
    }

    public ShieldBuilder with(@Nonnull Pattern pattern) {
        banner.addPattern(pattern);
        return this;
    }

    @Nonnull
    @Override
    public ItemStack build() {
        banner.update(true, false);
        meta.setBlockState(banner);
        stack.setItemMeta(meta);
        return stack;
    }

}
