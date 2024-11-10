package me.hapyl.fight.game.skin.trait;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class SkinTraitOnWin extends SkinTrait {

    public SkinTraitOnWin(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onWin(@Nonnull GamePlayer player);

}
