package me.hapyl.fight.enumclass;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.Tuple;

import javax.annotation.Nonnull;
import java.util.Set;

public class EnumClass {

    private static final Set<Class<?>> instantiatedClasses;

    static {
        instantiatedClasses = Sets.newHashSet();
    }

    public static <E extends EnumClass> boolean isInstantiated(@Nonnull Class<E> enumClass) {
        return instantiatedClasses.contains(enumClass);
    }

    public static <E extends EnumClass> void instantiate(@Nonnull Class<E> enumClass, @Nonnull EnumClassInspector inspector) {
        if (isInstantiated(enumClass)) {
            throw new IllegalArgumentException("Class '%s' has already been instantiated!".formatted(enumClass.getSimpleName()));
        }

        try {
            for (EnumClassMember member : inspector.members) {
                final Tuple<Boolean, String> validate = member.validate(enumClass);

                // Ok, continue
                if (validate.a()) {
                    continue;
                }

                // Error, throw
                throw new IllegalArgumentException("Unable to instantiate '%s': %s".formatted(enumClass.getSimpleName(), validate.b()));
            }

            // Mark as instantiated
            instantiatedClasses.add(enumClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
