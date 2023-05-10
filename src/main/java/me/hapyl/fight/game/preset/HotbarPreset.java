package me.hapyl.fight.game.preset;

public class HotbarPreset {

    public static final HotbarPreset DEFAULT = new HotbarPreset(
            HotbarItem.WEAPON,
            HotbarItem.ABILITY_1,
            HotbarItem.ABILITY_2,
            HotbarItem.ABILITY_3_COMPLEX,
            HotbarItem.ABILITY_4_COMPLEX,
            HotbarItem.ABILITY_5_COMPLEX,
            HotbarItem.HERO_ITEM,
            HotbarItem.EMPTY,
            HotbarItem.EXTRA_ITEM
    );

    private final HotbarItem[] items;

    HotbarPreset(HotbarItem... items) {
        if (items == null || items.length != 9) {
            throw new IllegalArgumentException("HotbarPreset must have 9 items!");
        }

        this.items = items;
    }

    public HotbarItem[] getItems() {
        return items;
    }
}
