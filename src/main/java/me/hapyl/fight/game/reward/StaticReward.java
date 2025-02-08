package me.hapyl.fight.game.reward;

public interface StaticReward {

    Reward HERO_RATING_FIRST_TIME = Reward.ofRepeatable("Hero Rating").withResource(RewardResource.COINS, 5_000);

}
