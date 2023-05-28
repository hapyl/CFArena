package me.hapyl.fight.game.ui.display;

import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;

public interface DisplayAnimation {

    /**
     * Play animation for the text display if needed.
     *
     * @param display - Text display.
     * @param tick    - Current tick from 0-max.
     * @param max     - Max ticks.
     * @return true to cancel display and remove it, false to ignore.
     */
    boolean animate(@Nonnull TextDisplay display, final int tick, final int max);

    static DisplayAnimation relative(double x, double y, double z) {
        return (display, tick, max) -> {
            display.teleport(display.getLocation().add(x, y, z));
            return false;
        };
    }

}
