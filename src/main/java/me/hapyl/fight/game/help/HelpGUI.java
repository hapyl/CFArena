package me.hapyl.fight.game.help;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StaticStyledGUI;
import me.hapyl.fight.gui.styled.StyledGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class HelpGUI extends StyledGUI {

    public HelpGUI(Player player, String subName) {
        super(player, menuArrowSplit("Help", subName), Size.FIVE);

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Help", HelpGeneral::new);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onUpdate() {
        final ItemStack border = new ItemBuilder(getBorder()).asIcon();

        fillRow(0, border);
        fillRow(4, border);

        StaticStyledGUI.setReturn(this);
    }

    @Nonnull
    public abstract Material getBorder();

}
