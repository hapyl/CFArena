package me.hapyl.fight.gui;

import me.hapyl.fight.game.setting.Setting;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

// FIXME (hapyl): 028, Sep 28: rewrite this you know how
public class SettingsGUI extends PlayerGUI {

    public SettingsGUI(Player player) {
        super(player, "Settings", 6);
        this.updateMenu();
    }

    private void updateMenu() {
        for (Setting setting : Setting.values()) {
            final boolean isEnabled = setting.isEnabled(this.getPlayer());
            this.setItem(setting.getSlot(), setting.createAsIcon(player));
            this.setItem(
                    setting.getSlot() + 9,
                    new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
                            .setName((isEnabled ? "&a" : "&c") + setting.getName())
                            .setLore("&7Click to " + (isEnabled ? "disable" : "enable"))
                            .asIcon()
            );

            final Action action = player -> {
                setting.setEnabled(player, !isEnabled);
                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                this.updateMenu();
            };

            this.setClick(setting.getSlot(), action);
            this.setClick(setting.getSlot() + 9, action);

        }

        this.openInventory();
    }

}
