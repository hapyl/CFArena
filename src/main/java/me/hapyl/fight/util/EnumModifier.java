package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Function;

public enum EnumModifier {

    PUBLIC(Modifier::isPublic),
    PRIVATE(Modifier::isPrivate),
    PROTECTED(Modifier::isProtected),
    STATIC(Modifier::isStatic),
    FINAL(Modifier::isFinal),
    SYNCHRONIZED(Modifier::isSynchronized),
    VOLATILE(Modifier::isVolatile),
    TRANSIENT(Modifier::isTransient),
    NATIVE(Modifier::isNative),
    INTERFACE(Modifier::isInterface),
    ABSTRACT(Modifier::isAbstract),
    STRICT(Modifier::isStrict);

    private final Function<Integer, Boolean> fn;

    EnumModifier(Function<Integer, Boolean> fn) {
        this.fn = fn;
    }

    /**
     * Returns {@code true} if the given {@link Member} has this {@link EnumModifier}.
     *
     * @param member - Member to check.
     * @return true if the given member has this modifier.
     */
    public boolean has(@Nonnull Member member) {
        return fn.apply(member.getModifiers());
    }

    /**
     * Returns {@code true} if the given {@link Member} has all the given {@link EnumModifier}.
     *
     * @param member    - Member to check.
     * @param modifiers - Modifiers.
     * @return true if the given member has all the given modifiers.
     */
    public static boolean has(@Nonnull Member member, @Nonnull EnumModifier... modifiers) {
        for (EnumModifier modifier : modifiers) {
            if (!modifier.has(member)) {
                return false;
            }
        }

        return true;
    }

}
