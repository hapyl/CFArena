package me.hapyl.fight.gui;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.crate.*;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerPageGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public class CrateGUI extends PlayerPageGUI<Crates> {

    private final int MAX_ITEMS_PREVIEW = 3;
    private final ItemStack NO_CRATES_ITEMS = new ItemBuilder(ItemStacks.OAK_QUESTION)
            .setName("&cNo Crates!")
            .addTextBlockLore("""
                                        
                    &6;;You don't have any crates!
                                        
                    Gain crates by playing the game and earning levels.
                    """)
            .asIcon();

    private final PlayerDatabase database;
    private final CrateEntry crates;
    private final CrateChest location;

    public CrateGUI(Player player, CrateChest location) {
        super(player, "Crates", 5);

        this.database = PlayerDatabase.getDatabase(player);
        this.crates = database.crateEntry;
        this.location = location;

        final Map<Crates, Long> crates = this.crates.getCrates();
        setContents(Lists.newArrayList(crates.keySet()));

        setEmptyContentsItem(NO_CRATES_ITEMS);

        openInventory(0);

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 0.75f);
        PlayerLib.playSound(player, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.75f);
    }

    @Override
    public void postProcessInventory(@Nonnull Player player, int page) {
        final Currency dust = Currency.CHEST_DUST;

        setCloseMenuItem(40);
        setItem(39, ItemBuilder.of(Material.CREEPER_BANNER_PATTERN, dust.getName(), "")
                .addSmartLore("Duplicate cosmetics will be converted into %s&7 and %s&7."
                        .formatted(
                                Currency.COINS.getFormatted(),
                                dust.getFormatted()
                        ), 40)
                .addLore()
                .addSmartLore("&bSpend %s&b to craft custom crates!".formatted(dust.getFormatted()))
                .addLore()
                .addLore("You have: %s", dust.format(format -> {
                    return format.getColor() + "%,d".formatted(database.currencyEntry.get(dust)) + format.getPrefixColored();
                }))
                .addLore()
                .addLore(Color.BUTTON + "Click to craft! &6&lSOON!&6™")
                .asIcon());
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Crates enumCrate, int index, int page) {
        final Crate crate = enumCrate.getCrate();
        final RandomLootSchema<Cosmetics> schema = crate.getSchema();
        final int crateCount = (int) crates.getCrates(enumCrate);

        final ItemBuilder builder = ItemBuilder.of(crate.getMaterial(), crate.getName(), crate.getDescription());

        builder.setAmount(crateCount);
        builder.addLore(Color.DEFAULT.bold() + "Crate Contents:");

        crate.getContents().forEach((rarity, items) -> {
            if (items.isEmpty()) {
                return;
            }

            builder.addLore(rarity + " &8" + schema.getDropChanceString(rarity));

            int count = 0;
            for (Cosmetics cosmetics : items) {
                if (count++ >= MAX_ITEMS_PREVIEW && items.size() > MAX_ITEMS_PREVIEW) {
                    builder.addLore(" &8...and %s more!", items.size() - MAX_ITEMS_PREVIEW);
                    break;
                }

                final boolean isUnlocked = cosmetics.isUnlocked(player);
                final String cosmeticName = cosmetics.getCosmetic().getName();

                if (isUnlocked) {
                    builder.addLore(
                            "&a✔ &7&m%s&b » %s",
                            cosmeticName,
                            rarity.getCompensationString()
                    );
                }
                else {
                    builder.addLore(" ⁃ " + cosmeticName);
                }
            }
        });

        builder.addLore();
        builder.addLore("&bYou have &l%,d&b crates!", crateCount);

        builder.addLore();
        builder.addLore(Color.BUTTON + "Click to open!");
        builder.addLore(Color.BUTTON + "Right Click to preview!");

        return builder.asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull Crates enumCrate, int index, int page, @Nonnull ClickType clickType) {
        final Crate crate = enumCrate.getCrate();

        if (clickType.isRightClick()) {
            new CrateDetailsGUI(player, crate, location);
        }
        else {
            if (location.isOccupied()) {
                player.closeInventory();
                location.sendOccupiedMessage(player);
                return;
            }

            if (crate.isOwnAllItems(player)) {
                new CrateConfirmGUI(player, enumCrate, location);
            }
            else {
                new CrateLoot(player, enumCrate, location);
            }
        }
    }
}
