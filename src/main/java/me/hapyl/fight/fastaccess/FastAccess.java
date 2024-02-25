package me.hapyl.fight.fastaccess;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.registry.EnumId;
import me.hapyl.fight.util.MaterialCooldown;
import me.hapyl.fight.util.PlayerItemCreator;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class FastAccess extends EnumId implements MaterialCooldown, PlayerItemCreator {

    static final Map<Integer, PlayerRank> slowRankMap = Map.of(
            0, PlayerRank.DEFAULT,
            1, PlayerRank.DEFAULT,
            2, PlayerRank.DEFAULT,

            3, PlayerRank.VIP,
            4, PlayerRank.VIP,
            5, PlayerRank.VIP,

            6, PlayerRank.PREMIUM,
            7, PlayerRank.PREMIUM,
            8, PlayerRank.PREMIUM
    );

    private final Category category;

    public FastAccess(@Nonnull String id, Category category) {
        super(id);

        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public abstract void onClick(@Nonnull Player player);

    public boolean shouldDisplayTo(@Nonnull Player player) {
        return true;
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Nonnull
    @Override
    public Material getCooldownMaterial() {
        return Material.COMMAND_BLOCK_MINECART;
    }

    @Nonnull
    public ItemBuilder create(@Nonnull Player player) {
        return new ItemBuilder(Material.STONE);
    }

    @Nonnull
    public ItemStack createAsButton(Player player) {
        return create(player)
                .addLore()
                .addLore(Color.BUTTON + "Left Click " + getFirstWord() + ".")
                .addLore(Color.BUTTON + "Right Click to edit.")
                .asIcon();
    }
}
