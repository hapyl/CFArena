package me.hapyl.fight.game.loadout;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HotbarLoadout {

    private final PlayerProfile profile;
    private final HotbarSlots[] loadout;

    public HotbarLoadout(PlayerProfile profile) {
        this.profile = profile;
        this.loadout = new HotbarSlots[9];

        initLoadout();
    }

    /**
     * Gets an inventory slot (0-8) from a given {@link HotbarSlots}.
     *
     * @param slot - Hotbar slot.
     * @return an inventory slot, or -1.
     */
    public int getInventorySlotBySlot(@Nonnull HotbarSlots slot) {
        for (int i = 0; i < loadout.length; i++) {
            if (loadout[i] == slot) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets a {@link HotbarSlot} by the given inventory slot.
     *
     * @param slot - Inventory slot.
     * @return HotbarSlot or null.
     */
    @Nullable
    public HotbarSlot bySlot(int slot) {
        final HotbarSlots enumHotbarItem = loadout[slot];
        return enumHotbarItem == null ? null : enumHotbarItem.get();
    }

    private void initLoadout() {
        loadout[0] = HotbarSlots.WEAPON;
        loadout[1] = HotbarSlots.TALENT_1;
        loadout[2] = HotbarSlots.TALENT_2;
        loadout[3] = HotbarSlots.TALENT_3;
        loadout[4] = HotbarSlots.TALENT_4;
        loadout[5] = HotbarSlots.TALENT_5;
        loadout[6] = HotbarSlots.HERO_ITEM;
        loadout[7] = null;
        loadout[8] = HotbarSlots.MAP_ITEM;
    }
}
