package me.hapyl.fight.game.heroes.archive.librarian;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

public class Grimoire {

    private final GamePlayer player;
    private int usedAtLevel;

    private GrimoireBook currentBook;

    public Grimoire(GamePlayer player) {
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

    public GamePlayer getPlayer() {
        return player;
    }

    public GrimoireBook getCurrentBook() {
        return currentBook;
    }

}
