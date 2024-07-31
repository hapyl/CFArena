package me.hapyl.fight.util;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class NumberMatcher<N extends Number, R> {

    private final N n;
    private final Class<R> retClass;

    private final Set<Match> matches;

    private Function<N, R> def;

    NumberMatcher(N n, Class<R> retClass) {
        this.n = n;
        this.retClass = retClass;
        this.matches = Sets.newHashSet();
    }

    public NumberMatcher<N, R> match(@Nonnull Predicate<N> predicate, @Nonnull R r) {
        matches.add(new Match(predicate, r));
        return this;
    }

    public NumberMatcher<N, R> def(@Nonnull Function<N, R> def) {
        this.def = def;
        return this;
    }

    @Nullable
    public R get() {
        for (Match match : matches) {
            if (match.predicate.test(n)) {
                return match.r;
            }
        }

        if (def == null) {
            throw new IllegalStateException("No default case!");
        }

        return def.apply(n);
    }

    protected boolean match(@Nonnull N a, @Nonnull N b) {
        throw new IllegalStateException("Matcher must override #match(N a, N b)!");
    }

    public static <R> NumberMatcher<Integer, R> of(@Nonnull Integer integer, @Nonnull Class<R> ret) {
        return new NumberMatcher<>(integer, ret) {
            @Override
            protected boolean match(@Nonnull Integer a, @Nonnull Integer b) {
                return a.intValue() == b.intValue();
            }
        };
    }

    private class Match {
        private final Predicate<N> predicate;
        private final R r;

        public Match(Predicate<N> predicate, R r) {
            this.predicate = predicate;
            this.r = r;
        }
    }

}
