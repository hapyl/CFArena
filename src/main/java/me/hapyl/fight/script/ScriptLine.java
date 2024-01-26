package me.hapyl.fight.script;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.Enums;
import me.hapyl.spigotutils.module.util.TypeConverter;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ScriptLine {

    private final String[] strings;

    public ScriptLine(String line) {
        strings = line.split(" ");

        if (strings.length == 0) {
            throw new ScriptException("String cannot be empty.");
        }
    }

    @Nonnull
    public String getKey() {
        return getValue(0).toString();
    }

    public boolean isKeyMatches(@Nonnull String key) {
        return key.equalsIgnoreCase(getKey());
    }

    @Nonnull
    public TypeConverter getValue(int index) {
        return index < 0 || index >= strings.length ? TypeConverter.from("") : TypeConverter.from(strings[index]);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T getValueOrThrow(int index, @Nonnull Class<T> clazz, @Nonnull Function<String, String> onError) {
        if (index < 0 || index >= strings.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: %1$s < 0 || %1$s >= %2$s".formatted(index, strings.length));
        }

        final String value = strings[index];

        if (clazz == String.class) {
            return (T) value;
        }
        else if (clazz == Integer.class) {
            return (T) tryParse(value, Integer::parseInt, onError);
        }
        else if (clazz == Double.class) {
            return (T) tryParse(value, Double::parseDouble, onError);
        }
        else if (clazz == Float.class) {
            return (T) tryParse(value, Float::parseFloat, onError);
        }
        else if (clazz == Material.class) {
            return (T) tryParse(value, v -> Enums.byName(Material.class, v), onError);
        }

        throw new IllegalArgumentException("Unsupported class: " + clazz.getSimpleName());
    }

    @Override
    public String toString() {
        return Chat.arrayToString(strings, 1);
    }

    private <T> T tryParse(String value, Function<String, T> fn, Function<String, String> errorFn) {
        T t;

        try {
            t = fn.apply(value);
        } catch (Exception ignored) {
            throw new ScriptException(errorFn.apply(value));
        }

        if (t == null) {
            throw new ScriptException(errorFn.apply(value));
        }

        return t;
    }


}
