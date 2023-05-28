package me.hapyl.fight.game.preset;

public enum HotbarItem {

    EMPTY("Empty Slot", "Nothing will be placed here."),
    WEAPON("Weapon", "Your weapon will be here."),
    ABILITY_1("First Ability", "Your a ability will be here."),
    ABILITY_2("Second Ability", "Your b ability will be here."),
    ABILITY_3_COMPLEX("Third Complex Ability", "If hero is complex and has at least 3 abilities, it will be placed here."),
    ABILITY_4_COMPLEX("Fourth Complex Ability", "If hero is complex and has at least 4 abilities, it will be placed here."),
    ABILITY_5_COMPLEX("Fifth Complex Ability", "If hero is complex and has at least 5 abilities, it will be placed here."),

    HERO_ITEM("Hero Specific Item", "If hero has extra item, it will be placed here."),
    EXTRA_ITEM("Extra Item", "If map has extra item, it will be placed here. This cannot be moved.", false);

    private final String name;
    private final String description;
    private final boolean canModify;

    HotbarItem(String name, String description) {
        this(name, description, true);
    }

    HotbarItem(String name, String description, boolean canModify) {
        this.name = name;
        this.description = description;
        this.canModify = canModify;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCanModify() {
        return canModify;
    }
}
