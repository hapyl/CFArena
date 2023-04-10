package me.hapyl.fight.game.heroes;

/**
 * Represents hero's origin.
 */
public enum Origin {

    NOT_SET("Not set."),
    UNKNOWN("Unknown"),
    ;

    private final String name;

    Origin(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
