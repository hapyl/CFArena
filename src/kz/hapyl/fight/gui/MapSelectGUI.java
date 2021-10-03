package kz.hapyl.fight.gui;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.maps.GameMap;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.maps.MapFeature;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MapSelectGUI extends GUI {

	private static MapSelectGUI staticGUI = new MapSelectGUI();

	private MapSelectGUI() {
		super("Map Selection", Math.min(GUI.getSmartMenuSize(GameMaps.getPlayableMaps()) + 3, 6));
		this.createItems();
	}

	private void createItems() {
		final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&f").toItemStack();
		final SmartComponent component = newSmartComponent();

		for (final GameMaps value : GameMaps.getPlayableMaps()) {
			final GameMap map = value.getMap();

			final ItemBuilder builder = new ItemBuilder(map.getMaterial())
					.setName("&a" + map.getName())
					.addLore("&8/map " + value.name().toLowerCase(Locale.ROOT), " &7&o")
					.addLore("")
					.addSmartLore(map.getInfo());

			if (!map.getFeatures().isEmpty()) {
				builder.addLore().addLore("&aMap Features:").addLore();
				for (final MapFeature feature : map.getFeatures()) {
					builder.addLore(" &b" + feature.getName());
					builder.addSmartLore(feature.getInfo(), "  &7&o");
				}
			}

			final ItemStack item = builder.addLore("").addLore("&eClick to select").build();
			component.add(item, player -> {
				final GameMaps currentMap = Manager.current().getCurrentMap();
				if (currentMap == value) {
					Chat.sendMessage(player, "&cAlready selected!");
					return;
				}

				Manager.current().setCurrentMap(value, player);
			});
		}

		component.fillItems(this);

		// fill border
		for (int i = 0; i < this.getSize(); i++) {
			if ((i < 8 || i >= this.getSize() - 8) || i % 9 == 0 || i % 9 == 8) {
				this.setItem(i, blackBar);
			}
		}

	}

	public static void openGUI(Player player) {
		Nulls.runIfNotNull(staticGUI, mapSelectGUI -> {
			mapSelectGUI.openInventory(player);
		});
	}

}
