package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nonnull;

public abstract class PlayerData {

    public final GamePlayer player;

    public PlayerData(@Nonnull GamePlayer player) {
        this.player = player;
    }

    /**
     * Removes all the necessary data.
     */
    public abstract void remove();

    /**
     * Removes all the necessary data for the given entity.
     *
     * @param entity - Entity.
     * @throws NotImplementedException if not supported by this data.
     */
    public void remove(@Nonnull LivingGameEntity entity) {
        throw new NotImplementedException();
    }

}
