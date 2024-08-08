package me.hapyl.fight.registry;

import me.hapyl.fight.util.Final;
import me.hapyl.eterna.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Pattern;

public class PatternId {

    private final Pattern pattern;
    @Nonnull
    private final Final<String> id;

    public PatternId(@Nonnull Pattern pattern) {
        this.pattern = pattern;
        this.id = new Final<>();
    }

    public PatternId(@Nonnull Pattern pattern, @Nonnull String string) {
        this(pattern);

        setId(string);
    }

    @Nonnull
    public String getId() throws IllegalStateException {
        return id.getOrThrow();
    }

    public void setId(@Nonnull String id) {
        this.id.set(id);
        checkPattern();
    }

    public boolean matches(@Nullable String id) {
        if (id == null) {
            return false;
        }

        return pattern.matcher(id).matches();
    }

    private void checkPattern() throws NullPointerException, IllegalArgumentException {
        Validate.notNull(pattern, "Pattern cannot be null");
        Validate.notNull(id.get(), "Id cannot be null");
        Validate.isTrue(
                pattern.matcher(id.getOrThrow()).matches(),
                "Id '%s' in %s does not match the pattern: %s".formatted(id.get(), getClass().getSimpleName(), pattern)
        );
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PatternId other = (PatternId) o;
        return Objects.equals(id.get(), other.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id.get());
    }
}
