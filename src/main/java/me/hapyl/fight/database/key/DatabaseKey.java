package me.hapyl.fight.database.key;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public interface DatabaseKey {

    Maker ENUM = new Maker(Pattern.compile("[A-Z0-9_]+"));
    Maker DEFAULT = new Maker(Pattern.compile("[a-zA-Z0-9_-]+"));

    @Nonnull
    String getKey();

    default boolean isKeyMatches(@Nonnull String string) {
        return getKey().equals(string);
    }

    default boolean isKeyMatchesAnyCase(@Nonnull String string) {
        return getKey().equalsIgnoreCase(string);
    }

    @Nonnull
    static DatabaseKey of(@Nonnull String string) {
        return DEFAULT.of(string);
    }

    @Nonnull
    static DatabaseKey ofEnum(@Nonnull String string) {
        return ENUM.of(string);
    }

    class Maker {
        private final Pattern pattern;

        Maker(@Nonnull Pattern pattern) {
            this.pattern = pattern;
        }

        @Nonnull
        public DatabaseKey of(@Nonnull String key) {
            if (!pattern.matcher(key).matches()) {
                throw new IllegalArgumentException("Key %s does not match the pattern %s!".formatted(key, pattern.pattern()));
            }

            return new DatabaseKeyImpl(key);
        }

    }

}
