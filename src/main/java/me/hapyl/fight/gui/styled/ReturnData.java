package me.hapyl.fight.gui.styled;

import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface ReturnData {

    @Nonnull
    String getName();

    @Nonnull
    Action<Player> getAction();

    /**
     * Gets the slot to put the return button to.
     * <br>
     * The actual slot is calculated via this formula:
     *
     * <pre>
     *     size - (9 - slot)
     * </pre>
     *
     * @return the slot to put the return button to.
     */
    int getSlot();

    @Nonnull
    static ReturnData of(@Nonnull String name, @Nonnull Action<Player> action) {
        return of(name, action, 1);
    }

    @Nonnull
    static ReturnData of(@Nonnull String name, @Nonnull Action<Player> action, int slot) {
        return new ReturnData() {
            @Nonnull
            @Override
            public String getName() {
                return name;
            }

            @Nonnull
            @Override
            public Action<Player> getAction() {
                return action;
            }

            @Override
            public int getSlot() {
                return slot;
            }
        };
    }

}
