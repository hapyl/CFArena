package me.hapyl.fight.gui;

import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class ModeSelectGUI extends PlayerGUI {
	public ModeSelectGUI(Player player) {
		super(player, "Mode Selection", Math.min(6, GUI.getSmartMenuSize(Modes.values()) + 2));
		createItems();
	}

	private void createItems() {
		final SmartComponent component = this.newSmartComponent();

		for (final Modes value : Modes.values()) {
			final CFGameMode mode = value.getMode();
			component.add(
					new ItemBuilder(mode.getMaterial())
							.setName((value.isSelected() ? "&a" : "&c") + mode.getName())
							.addLore()
							.addSmartLore(mode.getDescription(), 35)
							.addLore()
							.addLore("%s Players Required: &f%s", mode.isPlayerRequirementsMet() ? "&a✔" : "&c❌", mode.getPlayerRequirements())
							.addLore("Time Limit: &f%s", new SimpleDateFormat("mm:ss").format(mode.getTimeLimit() * 1000))
							.addLore()
							.hideFlags()
							.addLore(value.isSelected() ? "&eAlready selected" : "&eClick to select")
							.build(),
					player -> {
						if (value.isSelected()) {
							PlayerLib.villagerNo(player, "&cAlready selected!");
						}
						else {
							value.select();
							PlayerLib.villagerYes(player);
						}
					}
			);
		}

		component.fillItems(this);
		this.openInventory();
	}

}
