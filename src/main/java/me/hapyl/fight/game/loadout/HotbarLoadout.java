package me.hapyl.fight.game.loadout;

import me.hapyl.fight.database.entry.HotbarLoadoutEntry;
import me.hapyl.fight.game.profile.PlayerProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class HotbarLoadout {

    private static final HotbarSlots[] DEFAULT_LOADOUT = {
            HotbarSlots.WEAPON,
            HotbarSlots.TALENT_1,
            HotbarSlots.TALENT_2,
            HotbarSlots.TALENT_3,
            HotbarSlots.TALENT_4,
            HotbarSlots.TALENT_5,
            HotbarSlots.HERO_ITEM,
            null,
            HotbarSlots.MAP_ITEM,
    };

    private final PlayerProfile profile;
    private final HotbarLoadoutEntry entry;
    private HotbarSlots[] loadout;

    public HotbarLoadout(@Nonnull PlayerProfile profile) {
        this.profile = profile;
        this.entry = profile.getDatabase().hotbarEntry;
        this.loadout = new HotbarSlots[9];

        initLoadout();
    }

    public boolean isIdentical(@Nonnull HotbarSlots[] other) {
        if (loadout.length != other.length) {
            return false;
        }

        for (int i = 0; i < loadout.length; i++) {
            if (loadout[i] != other[i]) {
                return false;
            }
        }

        return true;
    }

    public void setLoadout(@Nonnull HotbarSlots[] newLoadout) {
        if (newLoadout.length != 9) {
            throw new IllegalArgumentException("Loadout must be of length 9.");
        }

        this.loadout = newLoadout;
        this.entry.saveArray(loadout);
    }

    /**
     * Gets an inventory slot (0-8) from a given {@link HotbarSlots}.
     *
     * @param slot - Hotbar slot.
     * @return an inventory slot.
     */
    public int getInventorySlotBySlot(@Nonnull HotbarSlots slot) {
        for (int i = 0; i < loadout.length; i++) {
            if (loadout[i] == slot) {
                return i;
            }
        }

        throw new IllegalArgumentException(slot + " is not supported!");
    }

    public void forEachTalentSlot(@Nonnull BiConsumer<HotbarSlots, Integer> consumer) {
        for (int i = 0; i < HotbarSlots.TALENT_SLOTS.length; i++) {
            consumer.accept(HotbarSlots.TALENT_SLOTS[i], i);
        }
    }

    public void forEach(@Nonnull BiConsumer<HotbarSlots, Integer> consumer) {
        for (int i = 0; i < loadout.length; i++) {
            consumer.accept(loadout[i], i);
        }
    }

    /**
     * Gets a {@link HotbarSlot} by the given inventory slot.
     *
     * @param slot - Inventory slot.
     * @return HotbarSlot or null.
     */
    @Nullable
    public HotbarSlots bySlot(int slot) {
        return (slot < 0 || slot > 8) ? null : loadout[slot];
    }

    /**
     * Gets a copy of this loadout.
     *
     * @return a copy of this loadout.
     */
    @Nonnull
    public HotbarSlots[] getLoadout() {
        final HotbarSlots[] newArray = new HotbarSlots[9];

        System.arraycopy(loadout, 0, newArray, 0, 9);
        return newArray;
    }

    private void initLoadout() {
        loadout = entry.getArray();

        int nulls = 0;
        for (HotbarSlots slot : loadout) {
            if (slot == null) {
                nulls++;
            }
        }

        // Load default loadout if first join
        if (nulls >= 9) {
            setLoadout(DEFAULT_LOADOUT);
        }
    }
}
