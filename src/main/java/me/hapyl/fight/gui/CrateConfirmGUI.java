package me.hapyl.fight.gui;

import me.hapyl.fight.game.cosmetic.crate.CrateLocation;
import me.hapyl.fight.game.cosmetic.crate.CrateLoot;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CrateConfirmGUI extends ConfirmGUI {

    private final Crates crate;
    private final CrateLocation location;

    public CrateConfirmGUI(Player player, Crates crate, CrateLocation location) {
        super(player, "YOU OWN EVERYTHING!!!");

        this.crate = crate;
        this.location = location;

        openInventory();
    }

    @Nonnull
    @Override
    public ItemStack quoteItem() {
        return ItemBuilder.of(Material.PAPER, "You own everything!", "&8You really do own every item!")
                .addTextBlockLore("""
                                                
                        &6&l;;You own every item from this crate!
                                        
                        Opening it will result in coin compensation; but your &d&lRNG&7 flex will be shown in chat!
                        """).asIcon();
    }

    @Override
    public void onConfirm(@Nonnull Player player) {
        final CrateLoot loot = new CrateLoot(player, crate);

        location.onOpen(loot);
    }

    @Override
    public void onCancel(@Nonnull Player player) {
        new CrateGUI(player, location);
    }
}
