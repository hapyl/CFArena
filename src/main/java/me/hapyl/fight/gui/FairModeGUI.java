package me.hapyl.fight.gui;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.FairMode;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class FairModeGUI extends StyledGUI {

    private static final int[] SLOTS = {
            13, 19, 28, 29, 30, 21, 22, 23, 32, 33, 34, 25
    };

    private final Manager manager;

    public FairModeGUI(Player player) {
        super(player, "Fair Mode", Size.FIVE);

        this.manager = Manager.current();
        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Game Management", GameManagementGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.ICON_FAIR_MODE.asIcon());

        final FairMode currentMode = manager.getFairMode();

        for (int i = 0; i < SLOTS.length; i++) {
            final FairMode fairMode = FairMode.values()[i];
            final boolean isCurrent = currentMode == fairMode;

            final ItemBuilder builder = new ItemBuilder(fairMode.isUnfair() ? Material.FILLED_MAP : Material.PAPER)
                    .setName(fairMode.getName())
                    .setAmount(Math.max(1, i - 1))
                    .addLore()
                    .addSmartLore(fairMode.getDescription())
                    .addLore();

            if (isCurrent) {
                builder.addLore(Color.SUCCESS + "Currently selected!");
                builder.glow();
            }
            else {
                builder.addLore(Color.BUTTON + "Click to select!");
            }

            setItem(SLOTS[i], builder.asIcon(), player -> setFairMode(fairMode));
        }
    }

    private void setFairMode(FairMode fairMode) {
        final FairMode currentFairMode = manager.getFairMode();

        if (currentFairMode == fairMode) {
            return;
        }

        manager.setFairMode(player, fairMode);
        update();
    }

}
