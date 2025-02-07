package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class PiHelper {

    public static final double PI = Math.PI;
    public static final double PI_2 = Math.PI * 2;
    public static final double HALF_PI = Math.PI / 2;

    private PiHelper() {
    }

    /**
     * Rotates a value around the given degrees, starting from {@code startDegree} and rotating up to {@code degreesToRotate}.
     * The method calls the provided {@code consumer} for each step in radians from the start angle to the target angle.
     *
     * @param startDegree     - The starting angle in degrees.
     * @param degreesToRotate - The number of degrees to rotate.
     * @param step            - The step increment in radians for each rotation iteration.
     * @param consumer        - A {@link Consumer} that accepts the current angle in radians at each step.
     */
    public static void rotate(double startDegree, double degreesToRotate, double step, @Nonnull Consumer<Double> consumer) {
        final double rx = Math.toRadians(startDegree);
        final double rz = Math.toRadians(startDegree + degreesToRotate);
        double d;

        for (d = rx; d < rz; d += step) {
            consumer.accept(d);
        }
    }

    public static double rad(double degree) {
        return Math.toRadians(degree);
    }

    public static double deg(double rad) {
        return Math.toDegrees(rad);
    }

    public static double sin(double d) {
        return Math.sin(d);
    }

    public static double cos(double d) {
        return Math.cos(d);
    }

}
