package me.hapyl.fight.game.crate.convert;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.crate.CrateLocation;
import me.hapyl.fight.game.crate.Crates;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class CrateConvertOperationGUI extends StyledGUI {

    private final int[][] rankBarSlots = {
            { 2, 11, 20, 38 },
            { 13, 22, 40 },
            { 6, 15, 24, 42 }
    };

    private final ItemStack[] rankBarItems = {
            ItemBuilder.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "").asIcon(),
            ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE, "").asIcon(),
            ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE, "").asIcon()
    };

    private final CrateConverts converts;
    private final CrateLocation location;
    private final PlayerDatabase database;

    public CrateConvertOperationGUI(Player player, CrateConverts converts, CrateLocation location) {
        super(player, "Converting " + converts.getNameStripColor(), Size.FIVE);

        this.converts = converts;
        this.location = location;
        this.database = CF.getDatabase(player);

        update();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Crate Converts", fn -> new CrateConvertGUI(player, location));
    }

    @Override
    public void onUpdate() {
        setHeader(new ItemBuilder(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName("Convert")
                .addLore()
                .addSmartLore("Use the buttons below to convert the crates!")
                .asIcon());

        setConvertItem(29, PlayerRank.DEFAULT, 1);
        setConvertItem(31, PlayerRank.VIP, 5);
        setConvertItem(33, PlayerRank.PREMIUM, 10);

        // Fill bars
        for (int i = 0; i < rankBarSlots.length; i++) {
            for (int slot : rankBarSlots[i]) {
                setItem(slot, rankBarItems[i]);
            }
        }
    }

    private void setConvertItem(int slot, PlayerRank rank, int conversionTimes) {
        final PlayerRank playerRank = PlayerRank.getRank(player);
        final CrateConvert convert = converts.getWrapped();
        final Crates product = convert.getConvertProduct();

        final int canConvertTimes = convert.canConvertTimes(player);
        final boolean playerHasRank = playerRank.isOrHigher(rank);
        final boolean canConvert = playerHasRank && canConvertTimes >= conversionTimes;

        if (product == null) {
            return;
        }

        final ItemBuilder builder = StyledTexture.CRATE_CONVERT.toBuilder()
                .setAmount(conversionTimes)
                .setName("Convert " + conversionTimes + " crates")
                .addLore()
                .addSmartLore(convert.getDescription())
                .addLore();

        // Requirements
        builder.addLore("Requirements:");
        if (rank != PlayerRank.DEFAULT) {
            builder.addLore(" &8- " + rank.getFormat().prefix() + " &7or higher " + BukkitUtils.checkmark(playerHasRank));
        }

        convert.appendRequirementsScaledToItemBuilder(builder, conversionTimes, database);

        // Receive
        builder.addLore();
        builder.addLore("You will receive:");
        builder.addLore(" &8+ " + product.formatProduct((long) convert.getConvertProductAmount() * conversionTimes));

        builder.addLore();
        builder.addLoreIf(Color.BUTTON + "Click to convert!", canConvert);
        builder.addLoreIf(Color.ERROR + "Cannot convert!", !canConvert);

        final ItemStack stack = builder.asIcon();

        if (canConvert) {
            setItem(slot, stack, pl -> {
                convert.tryConvert(pl, conversionTimes);
                update();
            });
        }
        else {
            setItem(slot, stack, pl -> {
                Chat.sendMessage(pl, "&cCannot convert!");
                PlayerLib.villagerNo(pl);
            });
        }

    }
}
