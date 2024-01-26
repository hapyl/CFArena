package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class Duration {

    private final long value;
    private final TimeUnit timeUnit;

    public Duration(long value, @Nonnull TimeUnit timeUnit) {
        this.value = value;
        this.timeUnit = timeUnit;
    }

    /**
     * Gets the raw value.
     *
     * @return the value.
     */
    public long getValue() {
        return value;
    }

    /**
     * Gets the value in {@link TimeUnit#NANOSECONDS}.
     *
     * @return the value in nanoseconds.
     */
    public long asNanos() {
        return timeUnit.toNanos(value);
    }

    /**
     * Gets the value in {@link TimeUnit#MILLISECONDS}.
     *
     * @return the value in milliseconds.
     */
    public long asMillis() {
        return timeUnit.toMillis(value);
    }

    /**
     * Gets the value in {@link TimeUnit#SECONDS}.
     *
     * @return the value in seconds.
     */
    public long asSeconds() {
        return timeUnit.toSeconds(value);
    }

    /**
     * Gets the value in {@link TimeUnit#MINUTES}.
     *
     * @return the value in minutes.
     */
    public long asMinutes() {
        return timeUnit.toMinutes(value);
    }

    /**
     * Gets the value in {@link TimeUnit#HOURS}.
     *
     * @return the value in hours.
     */
    public long asHours() {
        return timeUnit.toHours(value);
    }

    /**
     * Gets the value in {@link TimeUnit#DAYS}.
     *
     * @return the value in days.
     */
    public long asDays() {
        return timeUnit.toDays(value);
    }
}
