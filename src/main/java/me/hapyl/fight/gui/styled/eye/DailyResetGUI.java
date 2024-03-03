package me.hapyl.fight.gui.styled.eye;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DailyResetGUI extends StyledGUI {

    private static final int rubyPriceTotal = 3;
    private static final int[] slots = { 20, 31, 24 };

    private final boolean[] placedRubies;
    private final PlayerProfile profile;
    private final PlayerDatabase database;
    private final CurrencyEntry entry;
    private int usedRubies;

    public DailyResetGUI(Player player) {
        super(player, "Reset Bonds", Size.FIVE);

        this.profile = PlayerProfile.getProfileOrThrow(player);
        this.database = profile.getDatabase();

        this.entry = database.currencyEntry;
        this.placedRubies = new boolean[rubyPriceTotal];

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("The Eye", DailyGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(new ItemBuilder(Material.RED_GLAZED_TERRACOTTA)
                .setName("&cReset Bonds")
                .asIcon());

        for (int i = 0; i < placedRubies.length; i++) {
            final int index = i;
            final boolean isPlaced = placedRubies[index];
            final int slot = slots[index];

            if (isPlaced) {
                setItem(slot, new ItemBuilder(Material.REDSTONE)
                        .setName("&4💎 Ruby Slot")
                        .addLore("&8Inserted!")
                        .addLore()
                        .addSmartLore("A ruby is inserted in this slot.")
                        .addLore()
                        .addLore(Color.BUTTON + "Click to remove!")
                        .glow()
                        .asIcon(), player -> {
                    placedRubies[index] = false;
                    usedRubies--;

                    update();

                    // Fx
                    PlayerLib.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.0f);
                    PlayerLib.playSound(player, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.75f);
                });
            }
            else {
                final ItemBuilder builder = new ItemBuilder(Material.RED_CARPET)
                        .setName("&8💎 Ruby Slot")
                        .addLore()
                        .addSmartLore("A ruby slot must be inserted into this slot!")
                        .addLore();

                if (!entry.has(Currency.RUBIES, usedRubies + 1)) {
                    setItem(slot, builder
                            .addLore(Color.ERROR + "Not enough rubies!")
                            .asIcon(), player -> {
                        Notifier.error(player, "You don't have enough rubies!");
                        PlayerLib.villagerNo(player);
                    });
                }
                else {
                    setItem(slot, builder
                            .addLore(Color.BUTTON + "Click to insert!")
                            .asIcon(), player -> {
                        placedRubies[index] = true;
                        usedRubies++;

                        update();

                        // Fx
                        PlayerLib.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_HIT, 1.0f);
                        PlayerLib.playSound(player, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f);
                    });
                }

            }
        }

        // Fill candles
        for (int slot : slots) {
            while (slot <= 44) {
                slot += 9;

                setItem(slot, new ItemBuilder(Material.BLACK_CANDLE)
                        .setName("&0|")
                        .asIcon());
            }
        }

        // Reset button
        boolean canReset = true;

        for (boolean placedRuby : placedRubies) {
            if (!placedRuby) {
                canReset = false;
                break;
            }
        }

        if (canReset) {
            setItem(13, new ItemBuilder(Material.RED_DYE)
                    .setName("&cReset Bonds!")
                    .addLore()
                    .addTextBlockLore("""
                            &7&o;;I see you're ready to sacrifice these precious rubies...
                            """)
                    .addLore()
                    .addLore(Color.ERROR + "This will consumer %s rubies!".formatted(rubyPriceTotal))
                    .addLore(Color.ERROR + "New bonds will inherit the remaining duration!")
                    .addLore(Color.ERROR + "You can only reset once per day!")
                    .addLore()
                    .addLore(Color.BUTTON + "Click to reset!")
                    .asIcon(), player -> {
                if (!entry.has(Currency.RUBIES, rubyPriceTotal)) {
                    Notifier.error(player, "You somehow don't have the rubies anymore!");
                    player.closeInventory();
                    return;
                }

                entry.subtract(Currency.RUBIES, rubyPriceTotal);
                profile.getChallengeList().resetBonds();

                database.challengeEntry.markResetToday();

                player.closeInventory();

                // Fx
                Notifier.success(player, "Reset bonds!");
                Notifier.info(player, " &4- " + Currency.RUBIES.formatProduct(3L));

                PlayerLib.playSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);
                PlayerLib.playSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
            });
        }
    }

}
