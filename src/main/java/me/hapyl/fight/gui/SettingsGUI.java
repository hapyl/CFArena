package me.hapyl.fight.gui;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.setting.Category;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.profile.PlayerProfileGUI;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class SettingsGUI extends StyledGUI {

    private final int[] settingsSlots = {
            10, 11, 12, 13, 14, 15, 16,
            28, 29, 30, 31, 32, 33, 34
    };

    private Category selectedCategory;

    public SettingsGUI(Player player) {
        super(player, "Settings", Size.FIVE);

        selectedCategory = Category.GAMEPLAY;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        fillRow(0, ItemStacks.BLACK_BAR);

        // Update category
        final SmartComponent component = newSmartComponent();
        for (Category category : Category.values()) {
            final boolean isCurrent = category == selectedCategory;
            final ItemBuilder builder = category.create(player).addLore();

            if (isCurrent) {
                builder.addLore(Color.SUCCESS + "Currently selected!");
                builder.glow();
            }
            else {
                builder.addLore(Color.BUTTON + "Click to select!");
            }

            component.add(builder.asIcon(), player -> {
                if (isCurrent) {
                    Chat.sendMessage(player, "&cAlready selected!");
                    PlayerLib.villagerNo(player);
                    return;
                }

                selectedCategory = category;
                update();
            });
        }

        component.apply(this, SlotPattern.DEFAULT, 0);

        // Update Settings
        final List<Settings> settings = Settings.byCategory(selectedCategory);

        for (int i = 0; i < settings.size(); i++) {
            final Settings setting = settings.get(i);
            final boolean isEnabled = setting.isEnabled(player);

            if (i >= settingsSlots.length) {
                player.sendMessage(Color.ERROR + "There are too many settings in this category, report this!");
                break;
            }

            final int slot = settingsSlots[i];

            final Action clickAction = player -> {
                setting.setEnabled(player, !isEnabled);
                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                update();
            };

            setItem(slot, setting.createAsIcon(player), clickAction);
            setItem(
                    slot + 9,
                    ItemBuilder.of(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
                            .setName(setting.getName())
                            .addLore("&8This is a button :o")
                            .addLore("")
                            .addLore(Color.BUTTON + "Click to %s!", isEnabled ? "disable" : "enable")
                            .asIcon(), clickAction
            );

        }
    }

}
