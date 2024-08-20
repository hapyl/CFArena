package me.hapyl.fight.database.key;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * A key of an item to be stored in the database.
 * <br>
 * Replaces {@link Enum#name()} and <b>must</b> match the legacy enum name.
 */
public interface DatabaseKey {

    /**
     * An {@link Enum} key maker.
     *
     * @see #ofEnum(String)
     */
    Maker ENUM = new Maker(Pattern.compile("[A-Z0-9_]+"));

    /**
     * A default key maker.
     *
     * @see #of(String)
     */
    Maker DEFAULT = new Maker(Pattern.compile("[a-z0-9_]+"));

    /**
     * Gets the key.
     *
     * @return the key.
     */
    @Nonnull
    String key();

    /**
     * Gets the key.
     *
     * @return the key.
     * @deprecated prefer {@link #key()}.
     */
    @Nonnull
    @Deprecated
    default String getKey() {
        return key();
    }

    /**
     * Gets the key.
     *
     * @return the key.
     * @deprecated Lazy way to get the key, but a good fail-safe. Anyway, prefer {@link #key()}
     */
    @Nonnull
    @Deprecated
    String toString();

    /**
     * Returns {@code true} if the given {@link String} matches the {@link #key()} exactly.
     *
     * @param string - String to match.
     * @return true if the given string matches the key exactly.
     */
    default boolean isKeyMatches(@Nonnull String string) {
        return key().equals(string);
    }

    /**
     * Returns {@code true} if the given {@link String} matches the {@link #key()}, ignoring the case.
     *
     * @param string - String to match.
     * @return true if the given string matches the key, ignoring the case.
     */
    default boolean isKeyMatchesAnyCase(@Nonnull String string) {
        return key().equalsIgnoreCase(string);
    }

    /**
     * Returns an empty {@link DatabaseKey}.
     *
     * @return an empty database key.
     * @deprecated keys exist for a reason, use this for testing or development only
     */
    @Deprecated
    static DatabaseKey empty() {
        if (DatabaseKeyImpl.EMPTY == null) {
            DatabaseKeyImpl.EMPTY = new DatabaseKeyImpl("_");
        }

        return DatabaseKeyImpl.EMPTY;
    }

    /**
     * Constructs a {@link DatabaseKey} from the given {@link String} according to {@link #DEFAULT} {@link Maker}.
     *
     * @param string - String to construct.
     * @return a new database key.
     * @throws IllegalArgumentException if the given string does not match the maker's pattern.
     */
    @Nonnull
    static DatabaseKey of(@Nonnull String string) {
        return DEFAULT.of(string);
    }

    /**
     * Constructs a {@link DatabaseKey} from the given {@link String} according to {@link #ENUM} {@link Maker}.
     *
     * @param string - String to construct.
     * @return a new database key.
     * @throws IllegalArgumentException if the given string does not match the maker's pattern.
     */
    @Nonnull
    static DatabaseKey ofEnum(@Nonnull String string) {
        return ENUM.of(string);
    }

    final class Maker {
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
