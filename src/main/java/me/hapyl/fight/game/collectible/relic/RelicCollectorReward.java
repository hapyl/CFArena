package me.hapyl.fight.game.collectible.relic;

import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.spigotutils.module.util.RomanNumber;

public class RelicCollectorReward extends CurrencyReward {

    public RelicCollectorReward(int tier) {
        super("Relic Collector " + RomanNumber.toRoman(tier));
    }

}
