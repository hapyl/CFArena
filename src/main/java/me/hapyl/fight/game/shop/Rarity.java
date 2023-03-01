package me.hapyl.fight.game.shop;

public enum Rarity {

    UNSET("&4set rarity you idiot"),
    COMMON("&7Common"),
    UNCOMMON("&aUncommon"),
    RARE("&9Rare"),
    EPIC("&5Epic"),
    LEGENDARY("&6Legendary"),
    MYTHIC("&dMythic");

    private final String name;

    Rarity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
