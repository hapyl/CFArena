package me.hapyl.fight.game.cosmetic.skin.trait;

import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;

public abstract class SkinTrait implements Described {

    private String name;
    private String description;

    public SkinTrait() {
    }

    public SkinTrait(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }
}
