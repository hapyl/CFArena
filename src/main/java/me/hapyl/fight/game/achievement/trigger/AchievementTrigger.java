package me.hapyl.fight.game.achievement.trigger;

import org.bukkit.entity.Player;

public abstract class AchievementTrigger {

    public abstract boolean test(Player player);

}
