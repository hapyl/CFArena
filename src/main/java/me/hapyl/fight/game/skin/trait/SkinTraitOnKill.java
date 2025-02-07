package me.hapyl.fight.game.skin.trait;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class SkinTraitOnKill extends SkinTrait {

    public SkinTraitOnKill(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onKill(@Nonnull GamePlayer player, @Nonnull GameEntity victim);

}
