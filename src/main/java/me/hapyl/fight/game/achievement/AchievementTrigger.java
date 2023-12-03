package me.hapyl.fight.game.achievement;

import me.hapyl.fight.trigger.EntityTrigger;

public interface AchievementTrigger<T extends EntityTrigger> {

    boolean test(T trigger);


}
