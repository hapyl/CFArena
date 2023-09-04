package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.BiConsumer;

public final class DisplayFieldSerializer {

    public static final DisplayFieldFormatter DEFAULT_FORMATTER = new DisplayFieldFormatter() {
        @Nonnull
        @Override
        public String format(String key, String value) {
            return "%s: &f&l%s".formatted(key, value);
        }
    };
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

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

        // Test for copy
        if (provider instanceof DisplayFieldDataProvider dataProvider) {
            final List<DisplayFieldData> displayFieldData = dataProvider.getDisplayFieldData();

            if (displayFieldData.isEmpty()) {
                return;
            }

            displayFieldData.forEach(data -> {
                final String formatData = format(data.displayField, formatter, data.field, data.instance);

                if (!formatData.isEmpty()) {
                    builder.addLore(formatData);
                }
            });
        }
    }

    public static void serialize(ItemBuilder builder, DisplayFieldProvider provider) {
        serialize(builder, provider, DEFAULT_FORMATTER);
    }

    public static String formatField(Field field, Object instance, DisplayField display) {
        final double scale = display.scaleFactor();
        final String suffix = display.suffix();
        final boolean suffixSpace = display.suffixSpace();

        try {
            field.setAccessible(true);

            // Format field value
            final Object value = field.get(instance);
            String stringValue = "";

            if (value instanceof Double decimal) {
                final double scaled = decimal * scale;

                if (scaled % 1 == 0) {
                    stringValue = String.valueOf((int) scaled);
                }
                else {
                    stringValue = DECIMAL_FORMAT.format(scaled);
                }
            }
            // Integers are always considered as ticks, use short or long for other values
            // Integers are NOT scaled with the scale!
            else if (value instanceof Integer tick) {
                stringValue = Utils.decimalFormatTick(tick);
            }
            else if (value instanceof Number number) {
                stringValue = Utils.decimalFormat((int) number * scale);
            }
            else if (value instanceof String string) {
                stringValue = string;
            }

            return stringValue + (suffix.isBlank() || suffix.isEmpty() ? "" : (suffixSpace ? " " : "") + suffix);
        } catch (Exception e) {
            return "null";
        }
    }

    public static void forEachDisplayField(DisplayFieldProvider provider, BiConsumer<Field, DisplayField> consumer) {
        for (Field field : provider.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(DisplayField.class)) {
                continue;
            }

            final DisplayField displayField = field.getAnnotation(DisplayField.class);

            if (displayField == null) {
                continue;
            }

            consumer.accept(field, displayField);
        }
    }

    public static void copy(DisplayFieldProvider from, DisplayFieldDataProvider to) {
        if (from == null || to == null) {
            throw new NullPointerException("Cannot copy from/to null!");
        }

        forEachDisplayField(from, (f, df) -> {
            to.getDisplayFieldData().add(new DisplayFieldData(f, df, from));
        });
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

        final String stringValue = formatField(field, instance, display);

        return formatter.format(builder.toString().trim(), stringValue);
    }
}
