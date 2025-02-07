package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.MaterialCooldown;
import me.hapyl.fight.util.PlayerItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class FastAccess implements Keyed, MaterialCooldown, PlayerItemCreator {

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

    private final Key key;
    private final Category category;

    public FastAccess(@Nonnull String id, Category category) {
        this.key = Key.ofString(id);
        this.category = category;
    }

    @Nonnull
    @Override
    public Key getKey() {
        return key;
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

    private String firstWord;

    @Nonnull
    public ItemStack createAsButton(Player player) {
        if (firstWord == null) {
            final String stringKey = key.getKey();
            final String[] splits = stringKey.replaceFirst("_", " ").split(" ");

            firstWord = splits.length != 0 ? splits[0].toLowerCase() : "";
        }

        return create(player)
                .addLore()
                .addLore(Color.BUTTON + "Left Click " + firstWord + ".")
                .addLore(Color.BUTTON + "Right Click to edit.")
                .asIcon();
    }
}
