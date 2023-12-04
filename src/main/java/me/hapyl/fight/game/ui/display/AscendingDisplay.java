package me.hapyl.fight.game.ui.display;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public class AscendingDisplay extends StringDisplay {

    public static final DisplayAnimation ANIMATION = (display, tick, max) -> {
        final Location location = display.getLocation();
        final double y = Math.sin(Math.toRadians(1 - (double) tick / max)) * 0.8d;

        location.add(0, y, 0);
        display.teleport(location);
        return false;
    };

    public AscendingDisplay(@Nonnull String string, int stay) {
        super(string, stay);

        animation = ANIMATION;
    }

    @Override
    public short opaque(int tick) {
        return (short) (tick * 10);
    }
}
