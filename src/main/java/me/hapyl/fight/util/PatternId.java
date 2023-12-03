package me.hapyl.fight.util;

import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        if (id.isNull()) {
            throw new IllegalStateException("ID is not set for " + getClass().getSimpleName());

        }
        return id.get();
    }

    public void setId(@ForceLowercase @Nonnull String id) {
        this.id.set(id.toLowerCase());
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
                pattern.matcher(id.get()).matches(),
                "Id %s in %s does not match the pattern: %s".formatted(id, getClass().getSimpleName(), pattern)
        );
    }
}
