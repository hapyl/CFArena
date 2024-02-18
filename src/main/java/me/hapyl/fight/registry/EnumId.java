package me.hapyl.fight.registry;

import me.hapyl.spigotutils.module.chat.Chat;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class EnumId extends PatternId {

    public static final Pattern PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String stringName;
    private final String firstWord;

    public EnumId(@Nonnull String id) {
        super(PATTERN, formatString(id));

        this.stringName = Chat.capitalize(id.replace("_", " "));

        final String[] splits = this.stringName.split(" ");
        this.firstWord = splits.length != 0 ? splits[0].toLowerCase() : "";
    }

    @Nonnull
    public String getFirstWord() {
        return firstWord;
    }

    @Nonnull
    public String getStringName() {
        return stringName;
    }

    @Nonnull
    public static String formatString(@Nonnull String string) {
        return string
                .toUpperCase()                       // A-Z
                .replace(" ", "_"); // _
    }

}
