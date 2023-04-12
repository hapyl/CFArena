package me.hapyl.fight.game.achievement;

/**
 * Base achievement class.
 */
public class Achievement {

    private final String name;
    private final String description;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
