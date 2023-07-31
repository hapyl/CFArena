package me.hapyl.fight.game.preset;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

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
            HotbarItem.MAP_ITEM
    );

    private final HotbarItem[] items;

    HotbarPreset(HotbarItem... items) {
        if (items == null || items.length != 9) {
            throw new IllegalArgumentException("preset must contain 9 items and cannot be null");
        }

        this.items = items;
    }

    @Nonnull
    public HotbarItem[] getItems() {
        return items;
    }

    public int getSlot(@Nonnull HotbarItem item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item) {
                return i;
            }
        }

        return 0;
    }

    @Nonnull
    public static HotbarPreset getPreset(@Nonnull GamePlayer player) {
        return getPreset(player.getPlayer());
    }

    @Nonnull
    public static HotbarPreset getPreset(@Nonnull Player player) {
        return DEFAULT;
    }
}
