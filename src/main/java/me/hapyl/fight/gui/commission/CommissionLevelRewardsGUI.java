package me.hapyl.fight.gui.commission;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.fight.game.commission.CommissionRewardTable;
import me.hapyl.fight.game.maps.CommissionLevel;
import me.hapyl.fight.game.reward.DropChance;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Can't really be PageGUI because I don't want to make another wrapper and let's be
// real there isn't gonna be that many rewards so we need another page, right? -h
public class CommissionLevelRewardsGUI extends StyledGUI {

    private final CommissionLevel level;

    public CommissionLevelRewardsGUI(@Nonnull Player player, @Nonnull CommissionLevel level) {
        super(player, "%s Rewards".formatted(level.getName()), Size.FIVE);

        this.level = level;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of(menuArrowSplit("Commissions", level.getName()), player -> new CommissionLevelGUI(player, level));
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_COMMISSIONS.asIcon());

        final SmartComponent component = newSmartComponent();

        level.rewards().getRewardsSorted(CommissionRewardTable.RewardSort.COMMON_TO_RATE)
             .forEach((reward, chance) -> {
                 final ItemStack icon = reward.icon();
                 final DropChance dropChance = DropChance.of(chance);
                 final ItemBuilder builder = icon != null ? ItemBuilder.of(icon) : ItemBuilder.playerHeadUrl(dropChance.texture());

                 component.add(builder
                         .setName(reward.getName())
                         .addLore()
                         .addLore("Drop Chance")
                         .addLore(" %s".formatted(dropChance.format(chance)))
                         .asIcon());
             });

        component.apply(this, SlotPattern.INNER_LEFT_TO_RIGHT, 2);
    }
}
