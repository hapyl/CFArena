package me.hapyl.fight.enumclass;

import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.util.EnumModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.hapyl.fight.enumclass.EnumClassMember.ok;
import static me.hapyl.fight.enumclass.EnumClassMember.error;

public class EnumClassInspector {

    protected final List<EnumClassMember> members;

    public EnumClassInspector() {
        members = new ArrayList<>();
    }

    public EnumClassInspector hasMethod(@Nonnull String methodName, @Nonnull Class<?> expectedReturnType, @Nullable Class<?>... parameters) {
        members.add(new EnumClassMember() {
            @Override
            public <E extends EnumClass> Tuple<Boolean, String> validate(@Nonnull Class<E> clazz) {
                try {
                    final Method method = clazz.getDeclaredMethod(methodName);

                    // Validate modifiers
                    if (!EnumModifier.has(method, EnumModifier.PUBLIC, EnumModifier.STATIC)) {
                        return error("Method '%s' is missing 'public' or 'static' modifier!".formatted(methodName));
                    }

                    // Validate return type
                    final Class<?> returnType = method.getReturnType();

                    if (returnType != expectedReturnType) {
                        return error("Return type of '%s' must be '%s', not '%s'!".formatted(
                                methodName,
                                expectedReturnType.getSimpleName(),
                                returnType.getSimpleName()
                        ));
                    }

                    return ok();
                } catch (NoSuchMethodException noSuchMethodException) {
                    return error("Missing method: " + methodName);
                }
            }
        });
        return this;
    }

    public EnumClassInspector hasField(@Nonnull String fieldName, @Nonnull Class<?> expectedReturnType) {
        members.add(new EnumClassMember() {
            @Override
            public <E extends EnumClass> Tuple<Boolean, String> validate(@Nonnull Class<E> clazz) {
                try {
                    final Field field = clazz.getDeclaredField(fieldName);

                    if (!EnumModifier.has(field, EnumModifier.PUBLIC, EnumModifier.STATIC)) {
                        return error("Field '%s' is missing 'public', 'static' or 'final' modifier!".formatted(field));
                    }

                    final Class<?> fieldType = field.getType();

                    if (fieldType != expectedReturnType) {
                        return error("Field type of '%s' must be '%s', not '%s'!".formatted(
                                fieldName,
                                expectedReturnType.getSimpleName(),
                                fieldType.getSimpleName()
                        ));
                    }

                    return ok();
                } catch (NoSuchFieldException e) {
                    return error("Missing field: " + fieldName);
                }
            }
        });
        return this;
    }

}
