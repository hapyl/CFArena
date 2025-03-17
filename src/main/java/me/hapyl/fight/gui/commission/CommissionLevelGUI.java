package me.hapyl.fight.gui.commission;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.commission.EnumTier;
import me.hapyl.fight.game.commission.Tier;
import me.hapyl.fight.game.maps.CommissionLevel;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommissionLevelGUI extends StyledGUI {

    private final int[] SLOTS = {
            20, 21, 22, 23, 24
    };

    private final CommissionLevel level;

    public CommissionLevelGUI(@Nonnull Player player, @Nonnull CommissionLevel level) {
        super(player, menuArrowSplit("Commissions", level.getName()), Size.FIVE);

        this.level = level;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Commissions", CommissionGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_COMMISSIONS.asIcon());

        // Tiers
        for (EnumTier enumTier : EnumTier.values()) {
            final Tier tier = enumTier.tier();
            final ItemBuilder builder = ItemBuilder.playerHeadUrl(level.texture());
            final int ordinal = enumTier.ordinal();

            builder.setName("%s %s".formatted(level.getName(), RomanNumber.toRoman(ordinal + 1)));
            builder.setAmount(ordinal + 1);
            builder.addLore();
            builder.addTextBlockLore(level.getDescription());
            builder.addLore();

            // Show multipliers
            builder.addLore("%s Difficulty".formatted(enumTier.toString()));
            builder.addLore(" &8x%s &7Commission Experience".formatted(tier.decimalFormat(Tier::expMultiplier)));
            builder.addLore(" &8x%s &7Monster Levels".formatted(tier.decimalFormat(Tier::enemyLevelMultiplier)));

            builder.addLore();
            builder.addLore("Stating Cost:");
            builder.addLore(" &6%,d %s".formatted(tier.startingCost(), Currency.COINS.getFormatted()));

            builder.addLore();
            builder.addLore("&cCOMING SOON");

            setItem(SLOTS[ordinal], builder.asIcon());
        }

        // Rewards
        setItem(
                30, new ItemBuilder(Material.CHEST)
                        .setName("Rewards")
                        .addTextBlockLore("""
                                &8%s
                                
                                View every possible reward for this commission as well it's drop chance!
                                
                                %sClick to view!
                                """.formatted(level.getName(), Color.BUTTON))
                        .asIcon(), player -> new CommissionLevelRewardsGUI(player, level)
        );
    }

}
