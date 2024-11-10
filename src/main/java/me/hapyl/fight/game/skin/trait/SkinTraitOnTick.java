package me.hapyl.fight.game.skin.trait;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class SkinTraitOnTick extends SkinTrait {

    public SkinTraitOnTick(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onTick(@Nonnull GamePlayer player, int tick);

}
