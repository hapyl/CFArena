package me.hapyl.fight.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.crate.Crate;
import me.hapyl.fight.game.cosmetic.crate.CrateChest;
import me.hapyl.fight.util.Sortable;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerPageGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CrateDetailsGUI extends PlayerPageGUI<Cosmetics> {

    private final Crate crate;
    private final CrateChest location;
    private final Sortable<Cosmetics, Rarity> sortable;

    public CrateDetailsGUI(Player player, Crate crate, CrateChest location) {
        super(player, crate.getName(), 5);

        this.crate = crate;
        this.location = location;
        this.sortable = new Sortable<>(Rarity.class) {
            @Override
            public boolean isKeep(@Nonnull Cosmetics cosmetics, @Nonnull Rarity rarity) {
                return cosmetics.getRarity() == rarity;
            }
        };

        update();
    }

    private void update() {
        setContents(sortable.sort(crate.getContents().listAll()));
        openInventory(1);
    }

    @Override
    public void postProcessInventory(Player player, int page) {
        setArrowBack(40, "Crates", cl -> new CrateGUI(player, location));

        sortable.setSortItem(this, 39, (onClick, rarity) -> update());
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
        builder.addLore(cosmetics.isUnlocked(player)
                ? (Color.SUCCESS.color("✔ You own this item!"))
                : (Color.ERROR.color("❌ You don't own this item.")));

        return builder.asIcon();
    }

}
