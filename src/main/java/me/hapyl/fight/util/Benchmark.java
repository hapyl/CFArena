package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    private long start;
    private long end;

    public Benchmark() {
    }

    public void start() {
        if (start != 0L) {
            return;
        }

        start = System.nanoTime();
    }

    public void end() {
        if (end != 0L) {
            return;
        }

        end = System.nanoTime();
    }

    @Nonnull
    public BenchmarkResult getResult() {
        if (start <= 0L) {
            throw new IllegalStateException("not started");
        }

        if (end <= 0L) {
            end();
        }

        return new BenchmarkResult(end - start);
    }

    public static class BenchmarkResult extends Duration {

        public BenchmarkResult(long value) {
            super(value, TimeUnit.NANOSECONDS);
        }
    }

}
