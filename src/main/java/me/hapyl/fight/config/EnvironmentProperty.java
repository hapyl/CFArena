package me.hapyl.fight.config;

import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.fight.CF;

import javax.annotation.Nonnull;

public class EnvironmentProperty<T> {

    private final String name;
    private final Class<T> clazz;
    private final T defaultValue;

    private EnvironmentProperty(@Nonnull String name, @Nonnull Class<T> clazz, @Nonnull T defaultValue) {
        this.clazz = clazz;
        if (!(defaultValue instanceof Integer) && !(defaultValue instanceof Boolean)) {
            throw new IllegalArgumentException("The value must be either integer or boolean, not %s!".formatted(
                    defaultValue.getClass()
                                .getSimpleName()));
        }

        this.name = name.toLowerCase();
        this.defaultValue = defaultValue;
    }

    @Nonnull
    public String name() {
        return name;
    }

    @Nonnull
    public Class<T> clazz() {
        return clazz;
    }

    @Nonnull
    public T defaultValue() {
        return defaultValue;
    }

    @Nonnull
    public T value() {
        return CF.environment().document.getValue(this);
    }

    public boolean value(@Nonnull Object object) {
        final Object toSet;

        if (object instanceof Integer || object instanceof Boolean) {
            toSet = object;
        }
        else {
            if (clazz == Integer.class) {
                toSet = Numbers.getInt(object);
            }
            else if (clazz == Boolean.class) {
                toSet = Boolean.valueOf(object.toString());
            }
            else {
                throw new IllegalArgumentException("Illegal property class: " + clazz);
            }
        }

        // Don't change the environment is not needed, this ACTUALLY writes in the database right away, be it async
        if (toSet == value()) {
            return false;
        }

        CF.environment().document.setValue(this, toSet);
        return true;
    }

    @Nonnull
    public static EnvironmentProperty<Integer> ofInteger(@Nonnull String name, int defaultValue) {
        return new EnvironmentProperty<>(name, Integer.class, defaultValue);
    }

    public static EnvironmentProperty.BooleanEnvironmentProperty ofBoolean(@Nonnull String name, boolean defaultValue) {
        return new BooleanEnvironmentProperty(name, defaultValue);
    }

    public static class BooleanEnvironmentProperty extends EnvironmentProperty<Boolean> {
        private BooleanEnvironmentProperty(@Nonnull String name, boolean defaultValue) {
            super(name, Boolean.class, defaultValue);
        }

        public boolean isEnabled() {
            return value();
        }

        public void setEnabled(boolean enabled) {
            value(enabled);
        }
    }
}
