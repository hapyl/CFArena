package me.hapyl.fight.game.heroes;

// FIXME (hapyl): 004, Apr 4, 2023: Rework roles: standardize or add more or idk remove them or idk or idk idk
@Deprecated
public enum Role {

    MELEE("Melee", "Melee heroes are experts of dealing damage up close."),
    MELEE_RANGE("Melee and Range", "These heroes are both specialist in melee and range combat."),
    RANGE("Range", "Rangers are dead-eye shooters that can hold distance to strike."),
    ASSASSIN("Assassin", "Assassins are masters of murder, with fast cuts and getaways."),
    STRATEGIST("Strategist", "Strategists rely on their abilities, rather than their fighting skill to win."),

    NONE("Someone forgor ☠ to set the role", "Someone forgor ☠ to set the role");

    private final String name;
    private final String description;

    Role(String name, String description) {
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
