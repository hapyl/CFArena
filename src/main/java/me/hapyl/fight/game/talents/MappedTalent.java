package me.hapyl.fight.game.talents;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents a talent that has a mapped value to a player.
 *
 * @param <T> - Anything removable.
 */
public abstract class MappedTalent<T extends Removable> extends Talent {

    private final Map<Player, T> mapped;
    private boolean removeAtDeath;

    public MappedTalent(@Nonnull String name) {
        super(name);

        mapped = Maps.newHashMap();
        removeAtDeath = true;
    }

    public boolean isRemoveAtDeath() {
        return removeAtDeath;
    }

    public void setRemoveAtDeath(boolean removeAtDeath) {
        this.removeAtDeath = removeAtDeath;
    }

    @Override
    public void onStop() {
        mapped.clear();
    }

    @Override
    public final void onDeath(Player player) {
        if (!removeAtDeath) {
            return;
        }

        removeMapped(player);
    }

    /**
     * Gets the mapped value if present; null otherwise.
     *
     * @param player - Player.
     * @return the mapped value if present; null otherwise.
     */
    @Nullable
    public final T getMapped(Player player) {
        return mapped.get(player);
    }

    /**
     * Puts the mapped value before removing the existing one if present.
     *
     * @param player   - Player.
     * @param newValue - New value.
     * @return the new value.
     */
    public final T createMapped(Player player, T newValue) {
        final T oldMapped = removeMapped(player);

        if (oldMapped != null) {
            oldMapped.onReplace(player);
        }

        mapped.put(player, newValue);
        return newValue;
    }

    /**
     * Removes the current mapped value and returns it if present; null otherwise.
     *
     * @param player - Player.
     * @return the removed value or null.
     */
    @Nullable
    public final T removeMapped(Player player) {
        final T mapped = this.mapped.remove(player);

        if (mapped == null) {
            return null;
        }

        mapped.remove();
        return mapped;
    }

    /**
     * Returns true if player has a mapped value currently; false otherwise.
     *
     * @param player - Player.
     * @return true if player has a mapped value currently; false otherwise.
     */
    protected boolean isExists(Player player) {
        return mapped.containsKey(player);
    }
}
