package me.hapyl.fight.util.displayfield;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public final class DisplayFieldSerializer {

    public static final DisplayFieldFormatter DEFAULT_FORMATTER = new DisplayFieldFormatter() {
        @Nonnull
        @Override
        public String format(String key, String value) {
            return "%s: &f&l%s".formatted(key, value);
        }
    };

    public static void serialize(ItemBuilder builder, DisplayFieldProvider provider, DisplayFieldFormatter formatter) {
        for (Field field : provider.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(DisplayField.class)) {
                continue;
            }

            final DisplayField displayField = field.getAnnotation(DisplayField.class);
            final String formatDisplayField = format(displayField, formatter, field, provider);
            final String extra = displayField.extra();

            if (!formatDisplayField.isEmpty()) {
                builder.addLore(formatDisplayField);
            }

            if (!extra.isEmpty()) {
                builder.addSmartLore(extra, " &8&o");
            }
        }
    }

    public static void serialize(ItemBuilder builder, DisplayFieldProvider provider) {
        serialize(builder, provider, DEFAULT_FORMATTER);
    }

    private static String format(DisplayField display, DisplayFieldFormatter formatter, Field field, Object instance) {
        final String fieldName = field.getName();
        final StringBuilder builder = new StringBuilder();

        if (!display.name().isEmpty()) {
            builder.append(display.name());
        }
        // Format field name
        else {
            final char[] chars = fieldName.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];

                if (i == 0 || Character.isUpperCase(c)) {
                    builder.append(" ");
                    builder.append(Character.toUpperCase(c));
                    continue;
                }

                builder.append(Character.toLowerCase(c));
            }
        }

        try {
            field.setAccessible(true);

            // Format field value
            final Object value = field.get(instance);
            String stringValue = "";

            if (value instanceof Double decimal) {
                if (decimal % 1 == 0) {
                    stringValue = "" + decimal.intValue();
                }
                else {
                    stringValue = BukkitUtils.decimalFormat(decimal);
                }
            }
            // Integers are always considered as ticks, use short or long for other values
            else if (value instanceof Integer tick) {
                stringValue = BukkitUtils.roundTick(tick) + "s";
            }
            else if (value instanceof Number number) {
                stringValue = number.toString();
            }
            else if (value instanceof String string) {
                stringValue = string;
            }

            final String suffix = display.suffix();

            return formatter.format(builder.toString().trim(), stringValue) + ((suffix.isEmpty() || suffix.isBlank()) ? "" : " " + suffix);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "&4DisplayFieldSerializer Error!";
        }
    }

}
