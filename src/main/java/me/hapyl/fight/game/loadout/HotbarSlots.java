package me.hapyl.fight.game.loadout;

import me.hapyl.fight.util.EnumWrapper;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum HotbarSlots implements EnumWrapper<HotbarSlot> {

    WEAPON(new HotbarSlot(Material.IRON_SWORD, "Weapon", "Your weapon will be here. Your cursor will also snap here after using a talent.")),
    TALENT_1(new HotbarTalentSlot("First Talent", "Your first talent will be here.", 1)),
    TALENT_2(new HotbarTalentSlot("Second Talent", "Your second talent will be here.", 2)),
    TALENT_3(new HotbarTalentSlot("Third Talent", "Your third talent will be here.", 3)),
    TALENT_4(new HotbarTalentSlot("Fourth Talent", "Your fourth talent will be here.", 4)),
    TALENT_5(new HotbarTalentSlot("Fifth Talent", "Your fifth talent will be here.", 5)),
    HERO_ITEM(new HotbarSlot(
            Material.PLAYER_HEAD,
            "Hero-Specific Item", """
            Some heroes will use this slot for an item.
                        
            &aAs example:
            - Archer's BOOM BOW.
            - Ninja's Throwing Stars.
            - Dr. Ed's Gravity Gun, etc.
            """
    )),
    MAP_ITEM(new HotbarSlot(Material.FILLED_MAP, "Extra Item", "Some maps will use this slot for an item.", false));

    public static final HotbarSlots[] TALENT_SLOTS = {
            HotbarSlots.TALENT_1,
            HotbarSlots.TALENT_2,
            HotbarSlots.TALENT_3,
            HotbarSlots.TALENT_4,
            HotbarSlots.TALENT_5
    };

    private final HotbarSlot item;

    HotbarSlots(HotbarSlot item) {
        this.item = item;
        this.item.setHandle(this);
    }

    @Nonnull
    @Override
    public HotbarSlot get() {
        return item;
    }

    @Override
    public String toString() {
        return item.getName();
    }

    @Nullable
    public static HotbarSlots byIndex(int index) {
        return values()[index];
    }
}
