package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.entity.GamePlayer;

public abstract class PlayerData {

    public final GamePlayer player;

    public PlayerData(GamePlayer player) {
        this.player = player;
    }

    public abstract void remove();

}
