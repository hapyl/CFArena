package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;

public abstract class PlayerData {
    
    public final GamePlayer player;
    
    public PlayerData(@Nonnull GamePlayer player) {
        this.player = player;
    }
    
    /**
     * Removes all the necessary data.
     * <p>This method is called upon player's death to clear all the data and cancel the tasks, because a new {@link PlayerData} instance is created each time a player respawns.
     */
    public abstract void remove();
    
    /**
     * Removes all the necessary data for the given entity, who isn't this data player.
     *
     * @param entity - The entity who isn't this data player.
     * @throws NotImplementedException if not supported by this data.
     */
    public void remove(@Nonnull LivingGameEntity entity) {
        throw new NotImplementedException();
    }
    
}
