package kz.hapyl.fight.gui;

import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.util.CachedItemStack;
import kz.hapyl.fight.util.ItemStacks;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Locale;

public class HeroSelectGUI extends PlayerGUI {

	private final int guiFitSize = 21;

	public HeroSelectGUI(Player player) {
		super(player, "Hero Selection", 5);
		this.update(0);
	}

	private void update(int start) {
		final List<Heroes> values = Heroes.playable();
		this.clearItems();

		// this.fillOuter(ItemStacks.BLACK_BAR); // don't really looks good when icons are so close to each other

		// add previous menu button
		if (start >= guiFitSize) {
			this.setItem(38, ItemStacks.ARROW_PREV_PAGE, (player) -> {
				update(start - 21);
			});
		}

		if (values.size() - start > guiFitSize) {
			this.setItem(42, ItemStacks.ARROW_NEXT_PAGE, (player) -> {
				update(start + guiFitSize);
			});
		}

		this.setCloseMenuItem(40);

		for (int i = start, slot = 10; i < start + guiFitSize; i++, slot += slot % 9 == 7 ? 3 : 1) {
			if (i >= values.size()) {
				break;
			}

			final Heroes hero = values.get(i);
			if (hero == null) {
				slot -= slot % 9 == 7 ? 3 : 1;
				continue;
			}

			final CachedItemStack menuItem = hero.getHero().getMenuItem();

			if (!menuItem.isCached()) {
				menuItem.cache(new ItemBuilder(hero.getHero().getItem())
						.setName("&a" + Chat.capitalize(hero))
						.addLore("&8/hero " + hero.name().toLowerCase(Locale.ROOT))
						.addLore()
						.addSmartLore(hero.getHero().getAbout(), "&7&o", 35)
						.addLore()
						.addLore("&eLeft click to select")
						.addLore("&eRight click to view details")
						.toItemStack());
			}

			this.setItem(slot, menuItem.getItem());
			this.setClick(slot, (player) -> Manager.current().setSelectedHero(player, hero), ClickType.LEFT, ClickType.SHIFT_LEFT);
			this.setClick(slot, (player) -> new HeroPreviewGUI(player, hero), ClickType.RIGHT, ClickType.SHIFT_RIGHT);
		}

		this.openInventory();
	}
}
