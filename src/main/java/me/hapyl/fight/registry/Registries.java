package me.hapyl.fight.registry;

import com.google.common.collect.Sets;
import me.hapyl.fight.fastaccess.FastAccessRegistry;
import me.hapyl.fight.game.artifact.ArtifactRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

public final class Registries {

    public static final FastAccessRegistry FAST_ACCESS;
    public static final ArtifactRegistry ARTIFACTS;

    private static final Set<Registry<?>> registrySet;
    private static boolean initiated;

    static {
        registrySet = Sets.newHashSet();
        initiated = false;

        FAST_ACCESS = register(new FastAccessRegistry());
        ARTIFACTS = register(new ArtifactRegistry());
    }

    @SuppressWarnings({ "rawtypes" })
    public static void initiate() {
        if (initiated) {
            throw new IllegalStateException(Registries.class.getSimpleName() + " already has been initiated!");
        }

        initiated = true;

        registrySet.forEach(registry -> {
            final Class<? extends Registry> clazz = registry.getClass();

            for (Field field : clazz.getDeclaredFields()) {
                final RegistryItem annotation = field.getAnnotation(RegistryItem.class);

                if (annotation == null) {
                    continue;
                }

                final int modifiers = field.getModifiers();

                if (!Modifier.isPublic(modifiers)) {
                    throw new IllegalArgumentException("RegistryItem field must be public!");
                }

                if (!Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException("RegistryItem field must be final!");
                }

                if (Modifier.isStatic(modifiers)) {
                    throw new IllegalArgumentException("RegistryItem field cannot be static!");
                }

                try {
                    final Object object = field.get(registry);
                    final Method method = clazz.getMethod("register", Identified.class);

                    if (object instanceof Identified identified) {
                        method.invoke(registry, identified);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static <I extends Identified, R extends Registry<I>> R register(R r) {
        registrySet.add(r);
        return r;
    }

}
