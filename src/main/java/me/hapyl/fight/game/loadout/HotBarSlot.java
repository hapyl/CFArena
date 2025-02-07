package me.hapyl.fight.game.loadout;

import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.util.MaterialIcon;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public enum HotBarSlot implements Described, MaterialIcon, KeyedEnum {

    WEAPON(Material.IRON_SWORD, "Weapon", """
            Your weapon will be here.
            &8&o;;Your cursor will also snap here after using a talent.
            """
    ),
    TALENT_1(Material.CREEPER_BANNER_PATTERN, "First Talent", "Your first talent will be here."),
    TALENT_2(Material.CREEPER_BANNER_PATTERN, "Second Talent", "Your second talent will be here.") {
        @Override
        public int getItemAmount() {
            return 2;
        }
    },
    TALENT_3(Material.CREEPER_BANNER_PATTERN, "Third Talent", "Your third talent will be here.") {
        @Override
        public int getItemAmount() {
            return 3;
        }
    },
    TALENT_4(Material.CREEPER_BANNER_PATTERN, "Fourth Talent", "Your fourth talent will be here.") {
        @Override
        public int getItemAmount() {
            return 4;
        }
    },
    TALENT_5(Material.CREEPER_BANNER_PATTERN, "Fifth Talent", "Your fifth talent will be here.") {
        @Override
        public int getItemAmount() {
            return 5;
        }
    },
    HERO_ITEM(Material.PLAYER_HEAD,
            "Hero-Specific Item", """
            Some heroes will use this slot for an item.
            
            &aAs example:
            - Archer's BOOM BOW.
            - Ninja's Throwing Stars.
            - Dr. Ed's Gravity Gun, etc.
            """
    ),
    ARTIFACT(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, "Artifact", "Your artifact will be in this slot."),
    MAP_ITEM(Material.FILLED_MAP, "Map Item", "Some maps will use this slot for an item.");

    public static final HotBarSlot[] TALENT_SLOTS = {
            HotBarSlot.TALENT_1,
            HotBarSlot.TALENT_2,
            HotBarSlot.TALENT_3,
            HotBarSlot.TALENT_4,
            HotBarSlot.TALENT_5
    };

    private final Material material;
    private final String name;
    private final String description;

    HotBarSlot(@Nonnull Material material, @Nonnull String name, @Nonnull String description) {
        this.material = material;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nonnull
    @Override
    public Material getMaterial() {
        return material;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public int getItemAmount() {
        return 1;
    }
}
