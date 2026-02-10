package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.Described;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Allows for easier element value grab from an enum that wraps a value. (Registry Enum)
 *
 * @param <E> - Wrap type.
 */
public interface EnumWrapper<E> {

    @Nonnull
    E getWrapped();

    @Nonnull
    default String getName() {
        return cast(Described.class, Described::getName, "");
    }

    @Nonnull
    default String getNameStripColor() {
        return ChatColor.stripColor(getName());
    }

    @Nonnull
    default String getDescription() {
        return cast(Described.class, Described::getDescription, "");
    }

    private <T, R> R cast(Class<T> clazz, Function<T, R> function, R def) {
        final E e = getWrapped();

        if (clazz.isInstance(e)) {
            return function.apply(clazz.cast(e));
        }

        return def;
    }

}
