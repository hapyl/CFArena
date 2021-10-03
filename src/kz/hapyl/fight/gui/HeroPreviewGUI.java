package kz.hapyl.fight.gui;

import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.weapons.Weapon;
import kz.hapyl.fight.util.ItemStacks;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HeroPreviewGUI extends PlayerGUI {

	private final Heroes heroes;

	public HeroPreviewGUI(Player player, Heroes heroes) {
		super(player, "Hero Preview - " + heroes.getHero().getName(), 5);
		this.heroes = heroes;
		this.update();
	}

	public void update() {
		final Hero hero = this.heroes.getHero();
		final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&f").toItemStack();

		for (int i = 0; i < this.getSize(); i++) {
			if ((i < 8 || i >= this.getSize() - 8) || i % 9 == 0 || i % 9 == 8) {
				this.setItem(i, blackBar);
			}
		}

		this.setItem(
				18,
				ItemBuilder.playerHead(
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
						.setName("&aGo Back")
						.setLore("&7To Hero Selection")
						.toItemStack(),
				HeroSelectGUI::new
		);

		// TODO: 030. 08/30/2021 - add color for unlocked/locked hero
		this.setItem(11, new ItemBuilder(hero.getItem())
				.setName("&a%s", hero.getName())
				.addLore()
				.setSmartLore(hero.getAbout(), " &7&o")
				.toItemStack());

		this.setItem(29, hero.getWeapon().getItem());
		this.setItem(32, abilityItemOrNull(hero.getUltimate()));

		this.setItem(13, abilityItemOrNull(hero.getFirstTalent()));
		this.setItem(14, abilityItemOrNull(hero.getSecondTalent()));
		this.setItem(15, abilityItemOrNull(hero.getPassiveTalent()));

		if (hero instanceof ComplexHero complexHero) {
			this.setItem(22, abilityItemOrNull(complexHero.getThirdTalent()));
			this.setItem(23, abilityItemOrNull(complexHero.getFourthTalent()));
			this.setItem(24, abilityItemOrNull(complexHero.getFifthTalent()));
		}

		this.formatDebugInfo();
		this.fixAbilityItemsCount();
		this.openInventory();
	}

	private void formatDebugInfo() {
		if (!getPlayer().isOp()) {
			return;
		}

		final Hero hero = this.heroes.getHero();
		final Weapon weapon = hero.getWeapon();
		addLore(
				this.getInventory().getItem(29),
				"",
				"&cDebug:",
				" &bDamage: " + weapon.getDamage(),
				" &bId: " + (weapon.getId() == null ? "No Id" : weapon.getId())
		);

	}

	private void fixAbilityItemsCount() {
		for (int i = 0, slot = 13, amount = 1; i < 6; i++, slot += (slot % 9 == 6 ? 7 : 1)) {
			final ItemStack item = this.getInventory().getItem(slot);
			if (item == null) {
				continue;
			}
			item.setAmount(amount++);
		}
	}

	private ItemStack abilityItemOrNull(Talent talent) {
		if (talent == null) {
			return ItemStacks.AIR;
		}
		return talent.getItem();
	}

	private void addLore(ItemStack stack, Object... lore) {
		if (stack == null || stack.getItemMeta() == null) {
			return;
		}

		final ItemMeta meta = stack.getItemMeta();
		List<String> existingLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

		for (final Object str : lore) {
			existingLore.add(Chat.format(str == null ? "null" : str.toString()));
		}

		meta.setLore(existingLore);
		stack.setItemMeta(meta);
	}

}
