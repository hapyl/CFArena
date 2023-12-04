package me.hapyl.fight.game.ui.display;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public class DebuffDisplay extends StringDisplay {

    public static final DisplayAnimation ANIMATION = (display, tick, max) -> {
        final Location location = display.getLocation();
        final double y = Math.sin(Math.toRadians(1 - (double) tick / max)) * 0.6d;

        location.subtract(0, y, 0);
        display.teleport(location);
        return false;
    };

    public DebuffDisplay(@Nonnull String string, int stay) {
        super(string, stay);

        this.initTransformation = transformationScale(0.5f);
        this.animation = ANIMATION;
    }
}
