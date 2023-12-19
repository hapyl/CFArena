package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nonnull;

public abstract class PlayerData {

    public final GamePlayer player;

    public PlayerData(GamePlayer player) {
        this.player = player;
    }

    public abstract void remove();

    public void remove(@Nonnull LivingGameEntity entity) {
        throw new NotImplementedException();
    }

}
