package me.hapyl.fight.util;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.logging.Logger;

public class Benchmark {

    private static final Map<Class<?>, Short> benchmarks = Maps.newHashMap();

    private final Class<?> clazz;
    private long start;
    private long end;
    private long elapsed;

    public Benchmark(@Nonnull final Class<?> clazz) {
        this.clazz = clazz;
        benchmarks.compute(clazz, (c, i) -> Nulls.increment(i));
    }

    public long getElapsed() {
        return elapsed;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void start() {
        if (start != 0) {
            throw new IllegalStateException("already started");
        }
        start = System.nanoTime();
    }

    public void stop() {
        if (start == 0) {
            throw new IllegalArgumentException("not started");
        }

        end = System.nanoTime();
        elapsed = end - start;

        final Logger logger = Main.getPlugin().getLogger();

        logger.warning("Benchmark: %s (%s)".formatted(clazz.getSimpleName(), benchmarks.getOrDefault(clazz, (short) 0)));
        logger.warning(String.format("Took %sns", elapsed));
    }

}
