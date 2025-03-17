package me.hapyl.fight.gui.commission;

import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CommissionGUI extends StyledGUI {

    public CommissionGUI(@Nonnull Player player) {
        super(player, "Commissions", Size.FIVE);

        openInventory();
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_COMMISSIONS.asIcon());

        final SmartComponent component = newSmartComponent();

        EnumLevel.commissionLevels().forEach(level -> {
            component.add(level.create(player).addLore().addLore(Color.BUTTON + "Click for details!").asIcon(), player -> new CommissionLevelGUI(player, level));
        });

        component.apply(this, SlotPattern.DEFAULT, 2);
    }
}
