package me.hapyl.fight.game.cosmetic.skin.trait;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class SkinTraitOnStill extends SkinTrait {

    public SkinTraitOnStill(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onStandingStill(@Nonnull GamePlayer player);

}
