package me.hapyl.fight.game.cosmetic;

public enum Type {

    KILL("Executes whenever you eliminate an opponent."),
    DEATH("Executes whenever you die."),
    CONTRAIL("Trails behind you."),
    WIN(""),
    PREFIX("Prefixes the player's name."),

    ;

    private final String description;

    Type(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
