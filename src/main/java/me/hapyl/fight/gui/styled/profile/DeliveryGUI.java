package me.hapyl.fight.gui.styled.profile;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.delivery.Deliveries;
import me.hapyl.fight.game.delivery.Delivery;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class DeliveryGUI extends StyledGUI {

    public DeliveryGUI(Player player) {
        super(player, "Deliveries", Size.FOUR);

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        final List<Deliveries> unclaimedList = Deliveries.getUnclaimedDeliveries(player);

        if (unclaimedList.isEmpty()) {
            setItem(
                    22,
                    ItemBuilder.of(Material.MINECART, "&cEmpty!", "You don't have any deliveries!").asIcon(),
                    HumanEntity::closeInventory
            );
            return;
        }

        final SmartComponent component = newSmartComponent();
        for (Deliveries delivery : unclaimedList) {
            final Delivery handle = delivery.getHandle();

            component.add(ItemBuilder.of(handle.getMaterial(), handle.getName())
                    .addLore()
                    .addSmartLore(handle.getMessage())
                    .addLore()
                    .addLore(Color.BUTTON + "Click to claim!")
                    .asIcon(), player -> {
                delivery.deliver(player);
                update();
            });
        }

        component.apply(this, SlotPattern.INNER_LEFT_TO_RIGHT, 1);
    }
}
