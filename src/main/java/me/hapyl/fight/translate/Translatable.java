package me.hapyl.fight.translate;

import javax.annotation.Nonnull;

/**
 * Basically {@link me.hapyl.fight.util.Described} but now with {@link Language} support.
 */
public interface Translatable {

    /**
     * Gets the translated name for the given {@link Language}.
     *
     * @param language - Language.
     * @return the translated name.
     */
    @Nonnull
    default String getTranslateName(@Nonnull Language language) {
        final String key = getParentTranslatableKey0();

        return language.getTranslated(key + "name");
    }

    /**
     * Gets the translated description for the given {@link Language}.
     *
     * @param language - Language.
     * @return the translated description.
     */
    @Nonnull
    default String getTranslateDescription(@Nonnull Language language) {
        final String key = getParentTranslatableKey0();

        return language.getTranslated(key + "description");
    }

    /**
     * Gets the parent translatable key.
     *
     * @return the parent translatable key.
     */
    @Nonnull
    default String getParentTranslatableKey() {
        return "";
    }

    private String getParentTranslatableKey0() {
        final String key = getParentTranslatableKey();

        if (key.isEmpty() || key.endsWith(".")) {
            return key;
        }

        throw new IllegalArgumentException("Parent key must be either empty or end with a '.'!");
    }

}
