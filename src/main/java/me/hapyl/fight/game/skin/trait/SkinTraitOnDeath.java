package me.hapyl.fight.game.skin.trait;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SkinTraitOnDeath extends SkinTrait {

    public SkinTraitOnDeath(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onDeath(@Nonnull GamePlayer player, @Nullable GameEntity killer);

}
