package kz.hapyl.fight.game.heroes.storage.extra;

import kz.hapyl.fight.util.RomanNumber;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum GrimoireBook {

	NORMAL(1, Material.BOOK),
	KNOWLEDGE(2, Material.KNOWLEDGE_BOOK),
	SIGNED(3, Material.WRITTEN_BOOK),
	ENCHANTED(4, Material.ENCHANTED_BOOK);

	private final int bookLevel;
	private final Material book;
	private final ItemStack stack;

	GrimoireBook(int bookLevel, Material book) {
		this.bookLevel = bookLevel;
		this.book = book;
		this.stack = new ItemBuilder(this.book).setName("&aGrimoire &8&l" + RomanNumber.toRoman(this.bookLevel)).build();
	}

	public ItemStack getItem() {
		return stack;
	}

	public Material getBook() {
		return book;
	}

	public int getBookLevel() {
		return bookLevel;
	}

	public boolean isMaxed() {
		return this.getBookLevel() >= 4;
	}

	public GrimoireBook next() {
		return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
	}

	// static members
	public static boolean hasCooldown(Player player) {
		return player.hasCooldown(NORMAL.getBook());
	}

	public static int getCooldown(Player player) {
		return player.getCooldown(NORMAL.getBook());
	}

	public static String getCooldownString(Player player) {
		return BukkitUtils.roundTick(getCooldown(player));
	}

	public static void applyCooldown(Player player, int cd) {
		for (final GrimoireBook value : values()) {
			player.setCooldown(value.getBook(), cd);
		}
	}

	public static boolean isGrimmoreItem(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		final Material type = stack.getType();
		return type == NORMAL.getBook() || type == KNOWLEDGE.getBook() || type == SIGNED.getBook() || type == ENCHANTED.getBook();
	}

}
