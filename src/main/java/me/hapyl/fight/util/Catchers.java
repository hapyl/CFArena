package me.hapyl.fight.util;

import me.hapyl.fight.Main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class Catchers {

    private Catchers() {
    }

    private static final Pattern NO_COLOR_PATTERN = Pattern.compile("[§&][0-9a-fk-or]");

    public static void catchUFCE(@Nonnull String string, @Nullable Object... format) {
        try {
            string.formatted(format);
        } catch (RuntimeException e) {
            throw new CaughtException("Illegal format character: '%', for percents use '%%'!", e);
        }
    }

    public static void catchNull(@Nullable Object ultimate, @Nonnull String message) {
        if (ultimate == null) {
            throw new CaughtException(message);
        }
    }

    public static void catchNoColor(@Nonnull String string) {
        if (NO_COLOR_PATTERN.matcher(string).find()) {
            throw new CaughtException("String '%s' must not contain any color colors!".formatted(string.replace("\n", "")));
        }
    }

    private static class CaughtException extends RuntimeException {
        public CaughtException(String message, Throwable cause) {
            super(message, cause);
            printToLogger();
        }

        public CaughtException(String message) {
            super(message);
            printToLogger();
        }

        private void printToLogger() {
            final Logger logger = Main.getPlugin().getLogger();

            logger.severe("*****************");
            logger.severe("** CAUGHT YOU! **");
            logger.severe("*****************");

            final Throwable cause = getCause();

            if (cause != null) {
                cause.printStackTrace();
            }
        }
    }

}
