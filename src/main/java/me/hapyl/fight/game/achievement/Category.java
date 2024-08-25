package me.hapyl.fight.game.achievement;

/**
 * Represents an achievement category.
 */
public enum Category {

    GAMEPLAY("Gameplay", "General gameplay achievements."),
    TIERED("Tiered", "Tiered achievements can be completed multiple times."),
    HERO("Hero Related Achievements", "Hero-specific achievements."),

    ;

    private final String name;
    private final String description;

    Category(String name, String description) {
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
