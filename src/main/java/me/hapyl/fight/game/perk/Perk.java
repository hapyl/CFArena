package me.hapyl.fight.game.perk;

public enum Perk {

    LUCKY_7("Lucky Seven", "Most abilities have 7% change ot refresh instantly!"),
    ULTIMATE_MADNESS("Ultimate Madness", "Reduces points required for ultimate to charge by 5."),

    ;

    private final String name;
    private final String description;

    Perk(String name, String description) {
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
