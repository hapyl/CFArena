package me.hapyl.fight.database.entry;

public enum Currency {
    COINS("&6Coins"),
    RUBIES("&cRubies");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
