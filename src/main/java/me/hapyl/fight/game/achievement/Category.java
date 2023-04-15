package me.hapyl.fight.game.achievement;

/**
 * Represents an achievement category.
 */
public enum Category {

    GAMEPLAY("Gameplay"),
    ;

    private final String name;

    Category(String name) {
        this.name = name;
    }
}
