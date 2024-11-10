package me.hapyl.fight.util;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Duration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    private final Map<String, Long> steps;
    private long start;

    public Benchmark() {
        this.steps = Maps.newLinkedHashMap();
    }

    public void start() {
        if (start != 0L) {
            return;
        }

        start = System.nanoTime();
    }

    public void step(@Nonnull String name) {
        steps.put(name, System.nanoTime());
    }

    @Nonnull
    public List<BenchmarkResult> getResult() {
        if (start <= 0L) {
            throw new IllegalStateException("not started");
        }

        if (steps.isEmpty()) {
            throw new IllegalStateException("not steps");
        }

        final List<BenchmarkResult> results = new ArrayList<>();
        steps.forEach((name, result) -> results.add(new BenchmarkResult(name, result - start)));

        return results;
    }

    @Nonnull
    public BenchmarkResult getFirstResult() {
        return getResult().getFirst();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        getResult().forEach(result -> {
            builder.append("'%s' took %sms.".formatted(result.name, result.asMillis()));
            builder.append("\n");
        });

        return builder.toString();
    }

    public static class BenchmarkResult extends Duration {

        private final String name;

        public BenchmarkResult(String name, long value) {
            super(value, TimeUnit.NANOSECONDS);
            this.name = name;
        }

        @Override
        public String toString() {
            return "%s=%sms".formatted(name, asMillis());
        }
    }

}
