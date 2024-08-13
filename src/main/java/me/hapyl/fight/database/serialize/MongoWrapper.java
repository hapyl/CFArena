package me.hapyl.fight.database.serialize;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface MongoWrapper<T> {

    @Nonnull
    String wrapToString(@Nonnull T t);

    @Nonnull
    T unwrapFromString(@Nonnull String string);

    @Nonnull
    static <T> MongoWrapper<T> of(@Nonnull Function<T, String> toString, @Nonnull Function<String, T> fromString) {
        return new MongoWrapper<>() {
            @Nonnull
            @Override
            public String wrapToString(@Nonnull T t) {
                return toString.apply(t);
            }

            @Nonnull
            @Override
            public T unwrapFromString(@Nonnull String string) {
                return fromString.apply(string);
            }
        };
    }

    @Nonnull
    static <T extends Enum<T>> MongoWrapper<T> ofEnum(@Nonnull Class<T> enumClass) {
        return new MongoWrapper<>() {
            @Nonnull
            @Override
            public String wrapToString(@Nonnull T t) {
                return t.name();
            }

            @Nonnull
            @Override
            public T unwrapFromString(@Nonnull String string) {
                return Enum.valueOf(enumClass, string);
            }
        };
    }

}
