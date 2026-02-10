package me.hapyl.fight.annotate;

import me.hapyl.fight.CF;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that a class should be registered as a {@link Listener} if it implements the interface.
 * <p>This annotation does not perform registration automatically; it serves as a marker to indicate
 * that the class is intended to be registered manually by the developer.</p>
 *
 * @see Handler#register(Object)
 */
@Target(ElementType.TYPE)
public @interface AutoRegisteredListener {
    class Handler {
        /**
         * Registers the given object as a {@link Listener} if it implements the interface; does nothing otherwise.
         *
         * @param object – The object to potentially register as a listener.
         */
        public static void register(@Nonnull Object object) {
            if (object instanceof Listener listener) {
                CF.registerEvents(listener);
            }
        }
    }
}
