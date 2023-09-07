package me.hapyl.fight.game.achievement;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.spigotutils.module.util.RomanNumber;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Tier {

    private final int[] data;
    private final String roman;

    public Tier(int index, int tier, int reward) {
        this.data = new int[] { index, tier, reward };
        this.roman = RomanNumber.toRoman(index + 1);
    }

    public int getIndex() {
        return this.data[0];
    }

    public int getTier() {
        return this.data[1];
    }

    public int getReward() {
        return this.data[2];
    }

    public void reward(Player player) {
        PlayerDatabase.getDatabase(player).getCurrency().add(Currency.ACHIEVEMENT_POINT, getReward());
    }

    @Nonnull
    public String getRoman() {
        return roman;
    }

    @Override
    public String toString() {
        return "Tier{" +
                "index=" + getIndex() +
                ",tier=" + getTier() +
                ",reward=" + getReward() +
                '}';
    }
}
