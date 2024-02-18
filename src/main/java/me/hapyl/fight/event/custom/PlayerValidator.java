package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;

public interface PlayerValidator {

    default boolean validateHero(@Nonnull GamePlayer player, @Nonnull Hero hero) {
        return player.getHero() == hero;
    }

    default boolean validateHero(@Nonnull LivingGameEntity entity, @Nonnull Hero hero) {
        return entity instanceof GamePlayer player && validateHero(player, hero);
    }

}
