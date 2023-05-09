package me.hapyl.fight.game.collectible.relic;

import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.spigotutils.module.util.RomanNumber;

public class ExchangeReward extends CurrencyReward {
    public ExchangeReward(int tier) {
        super("Exchange Reward " + RomanNumber.toRoman(tier));
    }
}
