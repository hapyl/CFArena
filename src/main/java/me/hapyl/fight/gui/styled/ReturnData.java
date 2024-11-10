package me.hapyl.fight.gui.styled;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface ReturnData {

    @Nonnull
    String getName();

    @Nonnull
    Consumer<Player> getAction();

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
    static ReturnData of(@Nonnull String name, @Nonnull Consumer<Player> action) {
        return of(name, action, 1);
    }

    @Nonnull
    static ReturnData of(@Nonnull String name, @Nonnull Consumer<Player> action, int slot) {
        return new ReturnData() {
            @Nonnull
            @Override
            public String getName() {
                return name;
            }

            @Nonnull
            @Override
            public Consumer<Player> getAction() {
                return action;
            }

            @Override
            public int getSlot() {
                return slot;
            }
        };
    }

}
