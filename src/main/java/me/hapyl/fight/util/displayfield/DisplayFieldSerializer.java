package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.util.CFUtils;
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
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    /**
     * Serializes the display fields from the provider to the given item builder as lore.
     *
     * @param builder   - Builder.
     * @param provider  - Provider.
     * @param formatter - Formatter.
     */
    public static void serialize(@Nonnull ItemBuilder builder, @Nonnull DisplayFieldProvider provider, @Nonnull DisplayFieldFormatter formatter) {
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

    /**
     * Serializes the display fields from the provider to the given item builder as lore.
     *
     * @param builder  - Builder.
     * @param provider - Provider.
     */
    public static void serialize(@Nonnull ItemBuilder builder, @Nonnull DisplayFieldProvider provider) {
        serialize(builder, provider, DEFAULT_FORMATTER);
    }

    /**
     * Performs a field format with a given display field.
     *
     * @param field    - Field to format.
     * @param instance - Object instance.
     * @param display  - Display field.
     * @return the formatted string.
     */
    @Nonnull
    public static String formatField(@Nonnull Field field, @Nonnull Object instance, @Nonnull DisplayField display) {
        double scale = display.scaleFactor();
        String suffix = display.suffix();
        boolean suffixSpace = display.suffixSpace();

        if (display.percentage()) {
            scale = 100;
            suffix = "%";
            suffixSpace = false;
        }

        try {
            field.setAccessible(true);

            // Format field value
            final Object value = field.get(instance);
            String stringValue = "";

            if (value instanceof Double decimal) {
                stringValue = scaleFormat(decimal * scale);
            }
            // Integers are always considered as ticks, use short or long for other values
            // Integers are NOT scaled with the scale!
            else if (value instanceof Integer tick) {
                stringValue = CFUtils.decimalFormatTick(tick);
            }
            else if (value instanceof Number number) {
                stringValue = scaleFormat(number.intValue() * scale);
            }
            else if (value instanceof String string) {
                stringValue = string;
            }

            return stringValue + (suffix.isBlank() || suffix.isEmpty() ? "" : (suffixSpace ? " " : "") + suffix);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error whilst formatting DisplayField, see console!";
        }
    }

    /**
     * Performs a for each iteration for each field in the provider.
     *
     * @param provider - Provider.
     * @param consumer - Consumer.
     */
    public static void forEachDisplayField(@Nonnull DisplayFieldProvider provider, @Nonnull BiConsumer<Field, DisplayField> consumer) {
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

    /**
     * Copies fields from the provider to a data provider.
     * Usually done to copy fields from a hero class to an ultimate or other talents.
     *
     * @param from - Provider.
     * @param to   - Data provider.
     */
    public static void copy(@Nonnull DisplayFieldProvider from, @Nonnull DisplayFieldDataProvider to) {
        forEachDisplayField(from, (f, df) -> {
            to.getDisplayFieldData().add(new DisplayFieldData(f, df, from));
        });
    }

    private static String scaleFormat(double v) {
        if (v % 1 == 0) {
            return String.valueOf((int) v);
        }
        else {
            return DECIMAL_FORMAT.format(v);
        }
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
