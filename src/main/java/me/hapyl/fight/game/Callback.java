package me.hapyl.fight.game;

import javax.annotation.Nonnull;

/**
 * Represents a {@link java.util.function.Consumer} style callback with a {@link #callbackSelf(Object)} that can be used in constructors as follows:
 * <pre>{@code
 * class MyClass {
 *
 *     private MyClass callback;
 *
 *     MyClass(Callback<MyClass> callback) {
 *         this.callback = callback.callbackSelf(this);
 *     }
 * }
 * }</pre>
 *
 * @param <T> - The callback type.
 */
public interface Callback<T> {
    
    void callback(@Nonnull T t);
    
    @Nonnull
    default T callbackSelf(@Nonnull T t) {
        callback(t);
        return t;
    }
    
}
