package me.hapyl.fight.game.loadout;

import me.hapyl.fight.database.entry.HotbarLoadoutEntry;
import me.hapyl.fight.game.profile.PlayerProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class HotBarLoadout {

    private static final HotBarSlot[] DEFAULT_LOADOUT = {
            HotBarSlot.WEAPON,
            HotBarSlot.TALENT_1,
            HotBarSlot.TALENT_2,
            HotBarSlot.TALENT_3,
            HotBarSlot.TALENT_4,
            HotBarSlot.TALENT_5,
            HotBarSlot.HERO_ITEM,
            HotBarSlot.ARTIFACT,
            HotBarSlot.MAP_ITEM,
    };

    private final PlayerProfile profile;
    private final HotbarLoadoutEntry entry;

    private HotBarSlot[] loadout;

    public HotBarLoadout(@Nonnull PlayerProfile profile) {
        this.profile = profile;
        this.entry = profile.getDatabase().hotbarEntry;
        this.loadout = new HotBarSlot[9];

        initLoadout();
    }

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }

    public boolean isIdentical(@Nonnull HotBarSlot[] other) {
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

    /**
     * Gets an inventory slot (0-8) from a given {@link HotBarSlot}.
     *
     * @param slot - Hotbar slot.
     * @return an inventory slot.
     */
    public int getInventorySlotBySlot(@Nonnull HotBarSlot slot) {
        for (int i = 0; i < loadout.length; i++) {
            if (loadout[i] == slot) {
                return i;
            }
        }

        throw new IllegalArgumentException(slot + " is not supported!");
    }

    public void forEachTalentSlot(@Nonnull BiConsumer<HotBarSlot, Integer> consumer) {
        for (int i = 0; i < HotBarSlot.TALENT_SLOTS.length; i++) {
            consumer.accept(HotBarSlot.TALENT_SLOTS[i], i);
        }
    }

    public void forEach(@Nonnull BiConsumer<HotBarSlot, Integer> consumer) {
        for (int i = 0; i < loadout.length; i++) {
            consumer.accept(loadout[i], i);
        }
    }

    /**
     * Gets a {@link HotBarSlot} by the given inventory slot.
     *
     * @param slot - Inventory slot.
     * @return HotBar slot or null
     */
    @Nullable
    public HotBarSlot bySlot(int slot) {
        return (slot < 0 || slot > 8) ? null : loadout[slot];
    }

    /**
     * Gets a copy of this loadout.
     *
     * @return a copy of this loadout.
     */
    @Nonnull
    public HotBarSlot[] getLoadout() {
        final HotBarSlot[] newArray = new HotBarSlot[9];

        System.arraycopy(loadout, 0, newArray, 0, 9);
        return newArray;
    }

    public void setLoadout(@Nonnull HotBarSlot[] newLoadout) {
        if (newLoadout.length != 9) {
            throw new IllegalArgumentException("Loadout must be of length 9.");
        }

        this.loadout = newLoadout;
        this.entry.saveArray(loadout);
    }

    private void initLoadout() {
        loadout = entry.getArray();

        int nulls = 0;
        for (HotBarSlot slot : loadout) {
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
