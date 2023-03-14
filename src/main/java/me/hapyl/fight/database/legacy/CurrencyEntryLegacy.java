package me.hapyl.fight.database.legacy;

import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.CurrencyEntry;

public class CurrencyEntryLegacy extends CurrencyEntry {

    public CurrencyEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    public long getCoins() {
        return this.getConfigLegacy().getLong("currency.coins", 0L);
    }

    public String getCoinsString() {
        return String.format("%,d", getCoins());
    }

    public void addCoins(long amount) {
        this.setCoins(this.getCoins() + amount);
    }

    public void removeCoins(long amount) {
        this.setCoins(this.getCoins() - amount);
    }

    public void setCoins(long amount) {
        this.getConfigLegacy().set("currency.coins", amount);
    }

    //public void awardCoins(Award award) {
    //    addCoins(award.getCoins());
    //    Chat.sendMessage(this.getPlayer(), "&6&l+%s Coins &e(%s)", award.getCoins(), award.getReason());
    //    PlayerLib.playSound(this.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
    //}

}
