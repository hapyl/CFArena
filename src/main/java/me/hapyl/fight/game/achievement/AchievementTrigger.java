package me.hapyl.fight.game.achievement;

import me.hapyl.fight.trigger.PlayerTrigger;

public interface AchievementTrigger<T extends PlayerTrigger> {

    boolean test(T trigger);


}
