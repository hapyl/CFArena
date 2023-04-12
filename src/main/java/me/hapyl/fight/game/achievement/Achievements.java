package me.hapyl.fight.game.achievement;

public enum Achievements {

    //
    // READ BEFORE ADDING ACHIEVEMENT
    // 1. Make a good ENUM name, it should not be changed in the future.
    // 2. Make a good achievement name, something that is not too long.
    // 3. Make a good achievement description, how to get the achievement usually is advised.
    // 4. Use proper achievement class, Achievement for normal, ProgressAchievement for
    //    achievements that can be completed multiple times, HiddenAchievement for, well, hidden achievements.
    //

    PLAY_FIRST_GAME(new Achievement("That's How It Is", "")),
    ;

    private final Achievement achievement;

    Achievements(Achievement achievement) {
        this.achievement = achievement;
    }

    public Achievement getAchievement() {
        return achievement;
    }
}
