package kz.hapyl.fight.game.heroes.storage.extra;

import org.bukkit.entity.Player;

public class Grimoire {

	private final Player player;
	private int usedAtLevel;
	private GrimoireBook currentBook;

	public Grimoire(Player player) {
		this.player = player;
		this.usedAtLevel = 1;
		this.currentBook = GrimoireBook.NORMAL;
	}

	public void nextBook() {
		if (isMaxed()) {
			return;
		}
		currentBook = currentBook.next();
	}

	public void markUsedNow() {
		usedAtLevel = currentBook.getBookLevel();
	}

	public boolean isMaxed() {
		return currentBook != null && currentBook.isMaxed();
	}

	public int getUsedAtLevel() {
		return usedAtLevel;
	}

	public Player getPlayer() {
		return player;
	}

	public GrimoireBook getCurrentBook() {
		return currentBook;
	}
}
