package me.hapyl.fight.game.preset;

public enum HotbarItem {

    EMPTY("Empty Slot", "Nothing will be placed here.", -1),
    WEAPON("Weapon", "Your weapon will be here.", 0),
    ABILITY_1("First Ability", "Your first ability will be here.", 1),
    ABILITY_2("Second Ability", "Your second ability will be here.", 2),
    ABILITY_3_COMPLEX("Third Complex Ability", "Your third ability will be here.", 3),
    ABILITY_4_COMPLEX("Fourth Complex Ability", "Your fourth ability will be here.", 4),
    ABILITY_5_COMPLEX("Fifth Complex Ability", "Your fifth ability will be here.", 5),

    HERO_ITEM("Hero-Specific Item", "If a hero has extra item, it will be placed here.", 6),
    MAP_ITEM("Extra Item", "If a map has extra item, it will be placed here. This cannot be moved.", 8, false);

    private final String name;
    private final String description;
    private final boolean canModify;
    private final int slot;

    HotbarItem(String name, String description, int slot) {
        this(name, description, slot, true);
    }

    HotbarItem(String name, String description, int slot, boolean canModify) {
        this.name = name;
        this.description = description;
        this.canModify = canModify;
        this.slot = slot;
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

    public int getSlot() {
        return slot;
    }
}
