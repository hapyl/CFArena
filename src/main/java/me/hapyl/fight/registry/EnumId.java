package me.hapyl.fight.registry;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class EnumId extends PatternId {

    public static final Pattern PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    public EnumId(@Nonnull String id) {
        super(PATTERN, id);
    }

}
