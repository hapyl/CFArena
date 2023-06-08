package me.hapyl.fight.game.heroes;

/**
 * Represents hero's origin.
 */
public enum Origin {

    NOT_SET("Not set.", "Not set."),

    KINGDOM("Kingdom", "A royal kingdom."),
    UNKNOWN("Unknown", "The origin of this hero is a mystery..."),
    ;

    private final String name;
    private final String description;

    Origin(String name, String description) {
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
