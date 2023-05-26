package me.hapyl.fight.game.ui.display;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Random;

public class DamageDisplay {

    public static final String FORMAT = "%.1f";

    public DamageDisplay(@Nonnull Location location, double damage, boolean isCrit, final int stay) {
        final TextDisplay display = Entities.TEXT_DISPLAY.spawn(randomize(location), self -> {
            self.setBillboard(Display.Billboard.CENTER);
            self.setSeeThrough(true);
            self.setText(FORMAT.formatted(damage));
        });

        // Animate once
        transformScale(display, 2.0f, stay / 2);
        GameTask.runLater(() -> transformScale(display, 1.0f, stay / 2), stay / 2);

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= stay) {
                    display.remove();
                    cancel();
                    return;
                }

                // Animate
                final Location displayLocation = display.getLocation();

                if (tick < stay / 2) {
                    displayLocation.add(0.0d, BukkitUtils.GRAVITY / 2, 0.0d);
                }
                else {
                    displayLocation.subtract(0.0d, BukkitUtils.GRAVITY, 0.0d);
                }

                display.teleport(displayLocation);
                display.setTextOpacity((byte) (Byte.MAX_VALUE - tick));
            }
        }.runTaskTimer(0, 1);
    }

    public DamageDisplay(@Nonnull Location location, double damage, boolean isCrit) {
        this(location, damage, isCrit, 20);
    }

    private void transformScale(TextDisplay display, float scale, int duration) {
        final Transformation transformation = display.getTransformation();

        display.setTransformation(new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transformation.getRightRotation()
        ));

        display.setInterpolationDuration(duration);
    }

    private Location randomize(Location location) {
        return location.clone().add(randomDouble(), new Random().nextDouble() * 0.25d, randomDouble());
    }

    private double randomDouble() {
        final double random = new Random().nextDouble() * 1.5d;
        return new Random().nextBoolean() ? random : -random;
    }

}
