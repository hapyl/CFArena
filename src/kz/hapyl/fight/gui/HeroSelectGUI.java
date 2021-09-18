package kz.hapyl.fight.gui;

import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class HeroSelectGUI extends PlayerGUI {
	public HeroSelectGUI(Player player) {
		super(player, "Hero Selection", 4);
		this.update();
	}

	private void update() {
		final Heroes[] values = Heroes.values();

		for (int i = 0, slot = 10; i < this.getSize(); i++, slot += slot % 9 == 7 ? 3 : 1) {
			if (i >= values.length) {
				break;
			}
			final Heroes hero = values[i];
			if (hero == null) {
				slot -= slot % 9 == 7 ? 3 : 1;
				continue;
			}
			this.setItem(slot, new ItemBuilder(hero.getHero().getGuiTexture())
					.setName("&a" + Chat.capitalize(hero))
					.addLore().addSmartLore(hero.getHero().getAbout(), "&7&o")
					.addLore()
					.addLore("&eLeft click to select")
					.addLore("&eRight click to view details")
					.toItemStack());
			this.setClick(slot, (player) -> Manager.current().setSelectedHero(player, hero), ClickType.LEFT, ClickType.SHIFT_LEFT);
			this.setClick(slot, (player) -> new HeroPreviewGUI(player, hero), ClickType.RIGHT, ClickType.SHIFT_RIGHT);
		}

		this.openInventory();
	}
}
