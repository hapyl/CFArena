package me.hapyl.fight.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.crate.Crate;
import me.hapyl.fight.game.cosmetic.crate.CrateLocation;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.util.Filter;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrateDetailsGUI extends StyledPageGUI<Cosmetics> {

    private final Crate crate;
    private final CrateLocation location;
    private final Filter<Cosmetics, Rarity> filter;

    public CrateDetailsGUI(Player player, Crate crate, CrateLocation location) {
        super(player, crate.getName(), Size.FOUR);

        this.crate = crate;
        this.location = location;
        this.filter = new Filter<>(Rarity.class) {
            @Override
            public boolean isKeep(@Nonnull Cosmetics cosmetics, @Nonnull Rarity rarity) {
                return cosmetics.getRarity() == rarity;
            }
        };

        updateContents();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Crates", player -> new CrateGUI(player, location));
    }

    @Override
    public void onUpdate() {
        setHeader(new ItemBuilder(Material.TRAPPED_CHEST)
                .setName("Crate Preview")
                .addLore()
                .addSmartLore("Preview crate contents before opening it!")
                .asIcon());

        filter.setFilterItem(this, 39, (onClick, rarity) -> updateContents());
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Cosmetics cosmetics, int i, int i1) {
        final Cosmetic cosmetic = cosmetics.getCosmetic();
        final ItemBuilder builder = cosmetic.createItem(player);
        final Rarity rarity = cosmetic.getRarity();
        final float dropChance = crate.getSchema().getDropChance(rarity) / crate.getContents().getItemsAsCopy(rarity).size();

        builder.addLore();
        builder.addLore(Color.DEFAULT.color("Drop Chance: &l%.1f%%"), dropChance * 100);
        builder.addSmartLore("Drop chances displayed for individual items.", "&8&o");
        builder.addLore(cosmetics.isUnlocked(player)
                ? (Color.SUCCESS.color("✔ You own this item!"))
                : (Color.ERROR.color("❌ You don't own this item.")));

        return builder.asIcon();
    }

    private void updateContents() {
        setContents(filter.filter(crate.getContents().listAll()));
        openInventory(1);
    }

}
