package me.hapyl.fight.game.ui.display;

import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

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
    
    @Nonnull
    static DisplayAnimation sin(@Nonnull BiConsumer<Location, Double> consumer) {
        return (display, tick, max) -> {
            final Location location = display.getLocation();
            
            final double difference = 1 - ((double) tick / max * 0.8);
            final double sin = Math.sin(Math.toRadians(difference));
            
            consumer.accept(location, sin);
            display.teleport(location);
            
            return false;
        };
    }
    
    @Nonnull
    static DisplayAnimation sinAscend() {
        return sin((l, y) -> l.add(0, y, 0));
    }
    
    @Nonnull
    static DisplayAnimation sinDescend() {
        return sin((l, y) -> l.subtract(0, y, 0));
    }
    
}
