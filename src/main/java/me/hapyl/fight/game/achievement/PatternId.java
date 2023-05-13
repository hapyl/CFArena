package me.hapyl.fight.game.achievement;

import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class PatternId {

    private final Pattern pattern;
    private final String id;

    public PatternId(@Nonnull Pattern pattern, @ForceLowercase String id) throws IllegalArgumentException {
        this.pattern = pattern;
        this.id = id.toLowerCase();

        checkPattern();
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public boolean matches(@Nullable String id) {
        if (id == null) {
            return false;
        }

        return pattern.matcher(id).matches();
    }

    private void checkPattern() {
        Validate.notNull(pattern, "Pattern cannot be null");
        Validate.notNull(id, "Id cannot be null");
        Validate.isTrue(
                pattern.matcher(id).matches(),
                "Id %s in %s does not match the pattern: %s".formatted(id, getClass().getSimpleName(), pattern)
        );
    }
}
