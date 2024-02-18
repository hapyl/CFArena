package me.hapyl.fight.game.ui.display;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.Range;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base class to display text elements in the world.
 * <p>
 * This implementation uses the new TextDisplay entity.
 */
public class StringDisplay {

    protected final int stay;
    @Nullable protected DisplayAnimation animation;
    @Nonnull protected Transformation initTransformation = transformationScale(1.0f);
    @Nonnull protected String string;
    protected float viewRange;

    public StringDisplay(@Nonnull String string, final int stay) {
        this.string = string;
        this.stay = stay;
        this.viewRange = 16;
    }

    public StringDisplay(final int stay) {
        this("", stay);
    }

    @Nullable
    public DisplayAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(@Nullable DisplayAnimation animation) {
        this.animation = animation;
    }

    public void setViewRange(float viewRange) {
        this.viewRange = viewRange;
    }

    /**
     * Called once at {@link #display(Location)}
     *
     * @param display - Text display.
     */
    public void onStart(@Nonnull TextDisplay display) {
        display.setInterpolationDuration(stay);
    }

    /**
     * Called once before removing the display.
     *
     * @param display - Text display.
     */
    public void onEnd(@Nonnull TextDisplay display) {
    }

    /**
     * Gets the opaque value for this tick.
     *
     * @param tick - Current tick, 0-max;
     * @return the value to set.
     */
    @Range(max = 256)
    public short opaque(int tick) {
        return (short) (tick * 5);
    }

    public void onPrepare(@Nonnull TextDisplay display) {
    }

    /**
     * Starts display if {@link #string} is not empty nor blank.
     *
     * @param location - Location to spawn at.
     *                 Location will be tempered with {@link #getLocation(Location)}
     *                 before spawning text display. Override {@link #getLocation(Location)}
     *                 to change the behavior.
     */
    public void display(@Nonnull Location location) {
        if (string.isEmpty() || string.isBlank()) {
            return;
        }

        final TextDisplay text = Entities.TEXT_DISPLAY.spawn(getLocation(location), self -> {
            self.setBillboard(Display.Billboard.CENTER);
            self.setSeeThrough(true);
            self.setInterpolationDuration(0);
            self.setTransformation(initTransformation);
            self.setTextOpacity((byte) -1);
            self.setText(Chat.format(string));
            self.setViewRange(viewRange);

            onPrepare(self);
        }, false);

        onStart(text);

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                // Jesus this is a weird way of doing opacity
                short opaque = opaque(tick);

                if (opaque > Byte.MAX_VALUE) {
                    opaque -= 256;
                }

                text.setTextOpacity(Numbers.clampByte((byte) (-1 - opaque)));

                if (tick++ >= stay || (animation != null && animation.animate(text, tick, stay))) {
                    onEnd();
                }
            }

            private void onEnd() {
                StringDisplay.this.onEnd(text);

                cancel();
                text.remove();
            }
        }.runTaskTimer(0, 1).setShutdownAction(ShutdownAction.IGNORE);
    }

    @Nonnull
    public Location getLocation(Location location) {
        return location.clone().add(randomDouble(), new Random().nextDouble() * 0.25d, randomDouble());
    }

    private double randomDouble() {
        final double random = new Random().nextDouble();
        return new Random().nextBoolean() ? random : -random;
    }

    public static Transformation transformationScale(float xyz) {
        return transformationScale(xyz, xyz, xyz);
    }

    public static Transformation transformationScale(float x, float y, float z) {
        return new Transformation(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f),
                new Vector3f(x, y, z),
                new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f)
        );
    }

}
