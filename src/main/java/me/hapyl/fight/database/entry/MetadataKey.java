package me.hapyl.fight.database.entry;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class MetadataKey {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_.]*$");
    private static final String PARENT = "metadata.";

    private final String key;

    public MetadataKey(@Nonnull String key) {
        this.key = checkString(key);
    }

    @Nonnull
    public String getKey() {
        return PARENT + key;
    }

    private String checkString(String string) {
        if (!PATTERN.matcher(string).matches()) {
            throw new IllegalArgumentException("String in metadata does not match the pattern! (string=%s, pattern=%s)".formatted(
                    string,
                    PATTERN.toString()
            ));
        }

        return string.toLowerCase();
    }
}
