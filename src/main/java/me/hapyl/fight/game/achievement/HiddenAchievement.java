package me.hapyl.fight.game.achievement;

/**
 * Represents a hidden achievement.
 * For the most part a normal achievement but not shown in GUI until unlocked.
 */
public class HiddenAchievement extends Achievement {
    public HiddenAchievement(String name, String description) {
        super(name, description);
    }
}
