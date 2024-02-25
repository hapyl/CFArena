package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Timed;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A game task that has built-in geometry functions like: sin, cos, tan etc.
 */
public abstract class GeometryTask extends GameTask {

    public static final double PI_2 = Math.PI * 2;

    private final Properties properties;
    private int tick;
    private double theta;
    private int spin;

    public GeometryTask() {
        this.properties = new Properties(this);
    }

    /**
     * Gets this task properties.
     *
     * @return this task properties.
     */
    @Nonnull
    public Properties properties() {
        return properties;
    }

    /**
     * Gets a sine of the theta multiplied by the distance.
     *
     * @param distance - Distance.
     * @return a sine of the theta multiplied by the distance.
     */
    public double sin(double distance) {
        return Math.sin(theta) * distance;
    }

    /**
     * Gets a cosine of the theta multiplied by the distance.
     *
     * @param distance - Distance.
     * @return a cosine of the theta multiplied by the distance.
     */
    public double cos(double distance) {
        return Math.cos(theta) * distance;
    }

    /**
     * Gets a tangent of theta multiplied by the distance.
     *
     * @param distance - Distance.
     * @return a tangent of theta multiplied by the distance.
     */
    public double tan(double distance) {
        return Math.tan(theta) * distance;
    }

    /**
     * Gets the sine and cosine of theta multiplied by the distance as an array with 2 elements.
     *
     * @param distance - Distance.
     * @return the sine and cosine of theta multiplied by the distance as an array with 2 elements.
     */
    public double[] sin2cos(double distance) {
        return new double[] { sin(distance), cos(distance) };
    }

    /**
     * Gets the cosine and sine of theta multiplied by the distance as an array with 2 elements.
     *
     * @param distance - Distance.
     * @return the cosine and sine of theta multiplied by the distance as an array with 2 elements.
     */
    public double[] cos2sin(double distance) {
        return new double[] { cos(distance), sin(distance) };
    }

    /**
     * Offsets the location with sine and cosine multiplied by the distance.
     * The offset location is then passed through a callback before being restored to the original state.
     * <p>
     * <b>This is the behavior of circle around Y axis.</b>
     * <p>
     * Note that the given location will be actually modified during the callback,
     * hence both callback location and the original can be used in code, but it is
     * recommended to use a callback.
     *
     * @param location - Location to offset.
     * @param distance - Distance.
     * @param callback - Callback.
     */
    public void offsetXZ(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        offsetXZ(location, distance, 0, callback);
    }

    public void offsetXZCos(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] cos2sin = cos2sin(distance);

        location.add(cos2sin[0], 0, cos2sin[1]);
        callback.callback(location);
        location.subtract(cos2sin[0], 0, cos2sin[1]);
    }

    public void offsetXZ(@Nonnull Location location, double distance, double y, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(sin2cos[0], y, sin2cos[1]);
        callback.callback(location);
        location.subtract(sin2cos[0], y, sin2cos[1]);
    }

    /**
     * Offsets the location with sine and cosine multiplied by the distance.
     * The offset location is then passed through a callback before being restored to the original state.
     * <p>
     * <b>This is the behavior of circle around Z axis.</b>
     * <p>
     * Note that the given location will be actually modified during the callback,
     * hence both callback location and the original can be used in code, but it is
     * recommended to use a callback.
     *
     * @param location - Location.
     * @param distance - Distance.
     * @param callback - Callback.
     */
    public void offsetXY(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(sin2cos[0], sin2cos[1], 0);
        callback.callback(location);
        location.subtract(sin2cos[0], sin2cos[1], 0);
    }

    /**
     * Offsets the location with sine and cosine multiplied by the distance.
     * The offset location is then passed through a callback before being restored to the original state.
     * <p>
     * <b>This is the behavior of circle around X axis.</b>
     * <p>
     * Note that the given location will be actually modified during the callback,
     * hence both callback location and the original can be used in code, but it is
     * recommended to use a callback.
     *
     * @param location - Location.
     * @param distance - Distance.
     * @param callback - Callback.
     */
    public void offsetZY(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(0, sin2cos[1], sin2cos[0]);
        callback.callback(location);
        location.subtract(0, sin2cos[1], sin2cos[0]);
    }

    public abstract void run(double theta);

    /**
     * Gets the current tick of this task.
     *
     * @return the current tick of this task.
     */
    public int getTick() {
        return tick;
    }

    @Override
    public final void run() {
        if (properties.runIterations == 1) {
            run0();
        }
        else {
            for (int i = 0; i < properties.runIterations; i++) {
                if (run0()) {
                    cancel();
                    return;
                }
            }
        }
    }

    protected void offsetLocation(@Nonnull Location location, double x, double y, double z, @Nonnull Callback<Location> callback) {
        location.add(x, y, z);
        callback.callback(location);
        location.subtract(x, y, z);
    }

    private boolean shouldCancel() {
        // cancel if tick exceeds the max tick or if max spin is set and exceeds the max value
        if (tick > properties.maxTick || (properties.maxSpin != 0 && spin >= properties.maxSpin)) {
            return true;
        }

        final LivingGameEntity deathEntity = properties.deathEntity;

        if (deathEntity == null) {
            return false;
        }

        return deathEntity.isDead();
    }

    private boolean run0() {
        if (shouldCancel()) {
            cancel();
            return true;
        }

        tick += properties.tickIncrement;

        run(theta);

        if (theta > PI_2) {
            theta = 0;
            spin++;
        }
        else {
            theta += properties.step;
        }

        return false;
    }

    public static final class Properties {

        private final GeometryTask task;

        private int tickIncrement = 1;
        private int maxSpin;
        private int maxTick;
        private int runIterations = 1;
        private double step = Math.PI / 12;
        private LivingGameEntity deathEntity;

        private Properties(GeometryTask task) {
            this.task = task;
        }

        /**
         * Gets the task this property belongs to.
         *
         * @return the task this property belongs to.
         */
        public GeometryTask task() {
            return task;
        }

        /**
         * Sets the entity that this task will check for being alive before running each iteration of the code.
         *
         * @param entity - Entity to check for being alive.
         */
        public Properties cancelIfDead(@Nonnull LivingGameEntity entity) {
            deathEntity = entity;
            return this;
        }

        /**
         * Sets the tick increment for this task. The task will increment its tick by this value each iteration.
         *
         * @param inc - Increment.
         */
        public Properties inc(int inc) {
            this.tickIncrement = Math.max(inc, 1);
            return this;
        }

        /**
         * Sets how many times to run {@link #run(double)} code.
         * Useful if limited by ticks.
         * Don't forget to lower the step though!
         *
         * @param iterations - Times to run {@link #run(double)}
         */
        public Properties iterations(int iterations) {
            this.runIterations = Math.max(iterations, 1);
            return this;
        }

        /**
         * Sets the limit for "spins" or times theta will reach {@link #PI_2} before ending this task.
         * Note that the tick limit is also applied and can end the task before the first spin was made.
         *
         * @param spins - Max spins.
         */
        public Properties maxSpins(int spins) {
            this.maxSpin = spins;
            return this;
        }

        /**
         * Sets the tick limit before ending this task.
         *
         * @param timed - Timed.
         */
        public Properties max(@Nonnull Timed timed) {
            return max(timed.getDuration());
        }

        /**
         * Sets the tick limit before ending this task.
         *
         * @param max - Limit in minecraft ticks.
         */
        public Properties max(int max) {
            this.maxTick = Math.max(max, 1);
            return this;
        }

        /**
         * Sets the step by which theta will be incremented; must be lower than {@link #PI_2}.
         * <p>
         * When theta reaches {@link #PI_2}, it will be reset back to 0 and "spin" counts increments.
         *
         * @param step - Step.
         */
        public Properties step(double step) {
            this.step = Math.min(step, PI_2);
            return this;
        }

        public Properties infinite() {
            this.maxTick = Integer.MAX_VALUE - 1;
            return this;
        }

        // getters
        public int getTickIncrement() {
            return tickIncrement;
        }

        public int getMaxSpin() {
            return maxSpin;
        }

        public int getMaxTick() {
            return maxTick;
        }

        public int getRunIterations() {
            return runIterations;
        }

        public double getStep() {
            return step;
        }

        @Nullable
        public LivingGameEntity getDeathEntity() {
            return deathEntity;
        }
    }
}
