package kz.hapyl.fight.gui;

import kz.hapyl.fight.game.setting.Setting;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.Action;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsGUI extends PlayerGUI {

	public SettingsGUI(Player player) {
		super(player, "Settings", 6);
		this.updateMenu();
	}

	private void updateMenu() {
		for (Setting setting : Setting.values()) {
			final boolean isEnabled = setting.isEnabled(this.getPlayer());
			this.setItem(setting.getSlot(), this.buildMenuItem(setting));
			this.setItem(
					setting.getSlot() + 9,
					new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE).setName((isEnabled ? "&a" : "&c") + setting.getName())
							.setLore("&7Click to " + (isEnabled ? "disable" : "enable"))
							.toItemStack()
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

	public ItemStack buildMenuItem(Setting setting) {
		final boolean isEnabled = setting.isEnabled(this.getPlayer());
		return new ItemBuilder(setting.getMaterial())
				.setName((isEnabled ? ChatColor.GREEN : ChatColor.RED) + setting.getName())
				.addLore("&8This setting is currently " + (isEnabled ? "enabled" : "disabled"))
				.addLore()
				.addSmartLore(setting.getInfo())
				.addLore()
				.addLore("&eClick to " + (isEnabled ? "disable" : "enable"))
				.toItemStack();
	}
}
