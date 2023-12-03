package me.hapyl.fight.game.ui.display;

import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class DamageDisplay extends StringDisplay {

    public static final String FORMAT = "&b&l%.1f";
    public static final String FORMAT_CRIT = "&e&l%.1f&c&l✷";

    public static final DisplayAnimation ANIMATION = (display, tick, max) -> {
        final Location displayLocation = display.getLocation();

        if (tick < max / 2) {
            displayLocation.add(0.0d, BukkitUtils.GRAVITY / 2, 0.0d);
        }
        else {
            displayLocation.subtract(0.0d, BukkitUtils.GRAVITY, 0.0d);
        }

        display.teleport(displayLocation);
        return false;
    };

    private final boolean isCrit;

    public DamageDisplay(double damage, boolean isCrit) {
        super(isCrit ? FORMAT_CRIT.formatted(damage) : FORMAT.formatted(damage), isCrit ? 30 : 20);

        this.isCrit = isCrit;
        this.animation = ANIMATION;
        this.initTransformation = transformationScale(0.0f);
    }

    @Override
    public void onStart(@Nonnull TextDisplay display) {
        transformScale(display, isCrit ? 1.5f : 1.0f, stay);
    }

    private void transformScale(TextDisplay display, float scale, int duration) {
        final Transformation transformation = display.getTransformation();

        display.setInterpolationDuration(duration);
        display.setTransformation(new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transformation.getRightRotation()
        ));
    }

}
