package me.hapyl.fight.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.maps.Selectable;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class GameManagementSubGUI<T extends Enum<T> & Selectable> extends StyledGUI {

    private final List<T> values;

    public GameManagementSubGUI(Player player, String name, Size size, T[] values) {
        this(player, name, size, List.of(values));
    }

    public GameManagementSubGUI(Player player, String name, Size size, List<T> values) {
        super(player, name, size);
        this.values = values;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Game Management", GameManagementGUI::new);
    }

    @Nonnull
    public String getButton(@Nonnull T t, boolean isSelected) {
        return isSelected ? Color.SUCCESS + "Already Selected!" : Color.BUTTON + "Click to select!";
    }

    public int getStartIndex() {
        return 1;
    }

    @Nonnull
    public abstract ItemStack getHeaderItem();

    @Nonnull
    public abstract ItemBuilder createItem(@Nonnull T t, boolean isSelected);

    @Override
    public final void onUpdate() {
        final SmartComponent component = newSmartComponent();

        setHeader(getHeaderItem());

        for (T value : values) {
            final boolean isSelected = value.isSelected(player);
            final ItemBuilder builder = createItem(value, isSelected).addLore();
            final String buttonText = getButton(value, isSelected);

            builder.addLore(buttonText);

            if (isSelected) {
                builder.glow();
                component.add(builder.asIcon(), player -> {
                    Chat.sendMessage(player, "&cAlready selected!");
                    PlayerLib.villagerNo(player);
                    update();
                });
            }
            else {
                component.add(builder.asIcon(), player -> {
                    value.select(player);
                    PlayerLib.plingNote(player, 2.0f);
                    update();
                });
            }
        }

        component.apply(this, SlotPattern.DEFAULT, getStartIndex());
    }
}
