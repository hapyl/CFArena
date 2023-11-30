package me.hapyl.fight.gui;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CrateEntry;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.crate.*;
import me.hapyl.fight.game.cosmetic.crate.convert.CrateConvertGUI;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public class CrateGUI extends StyledPageGUI<Crates> {

    private final int MAX_ITEMS_PREVIEW = 3;
    private final ItemStack NO_CRATES_ITEMS = new ItemBuilder(ItemStacks.OAK_QUESTION)
            .setName("&cNo Crates!")
            .addTextBlockLore("""
                                        
                    &6;;You don't have any crates!
                                        
                    Gain crates by playing the game and earning levels.
                    """)
            .asIcon();

    private final PlayerDatabase database;
    private final CrateEntry entry;
    private final CrateChest location;

    public CrateGUI(Player player, CrateChest location) {
        super(player, "Crates", Size.FOUR);

        this.database = PlayerDatabase.getDatabase(player);
        this.entry = database.crateEntry;
        this.location = location;

        final Map<Crates, Long> crates = this.entry.mapped();

        setContents(Lists.newLinkedList(crates.keySet()));
        setEmptyContentsItem(NO_CRATES_ITEMS);

        update();

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_CHEST_OPEN, 0.75f);
        PlayerLib.playSound(player, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.75f);
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, Crates enumCrate, int index, int page) {
        final Crate crate = enumCrate.getCrate();
        final RandomLootSchema<Cosmetics> schema = crate.getSchema();
        final int crateCount = (int) entry.getCrates(enumCrate);

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

    @Override
    public void onUpdate() {
        final Currency dust = Currency.CHEST_DUST;

        setCloseMenuItem(40);
        setPanelItem(
                3,
                StyledTexture.CRATE_CONVERT.toBuilder()
                        .setName("Crate Conversion")
                        .addTextBlockLore(
                                """
                                        Duplicate cosmetics will be converted into %1$s&7 and %2$s&7.
                                                          
                                        Spend %2$s&7 to convert and craft crates!
                                        """,
                                Currency.COINS.getFormatted(),
                                dust.getFormatted()
                        )
                        .addLore()
                        .addLore("You have: %s", dust.format(format -> {
                            return format.getColor() + "%,d".formatted(database.currencyEntry.get(dust)) + format.getPrefixColored();
                        }))
                        .addLore()
                        .addLore(Color.BUTTON + "Click to convert!")
                        .asIcon(),
                fn -> new CrateConvertGUI(player, location)
        );
    }
}
