package me.hapyl.fight.translate;

import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public class TranslatedDescribed implements Described {

    private final String name;
    private final String description;

    public TranslatedDescribed(Language language, String key) {
        this.name = language.getTranslated(key + ".name");
        this.description = language.getTranslated(key + ".description");
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }
}
