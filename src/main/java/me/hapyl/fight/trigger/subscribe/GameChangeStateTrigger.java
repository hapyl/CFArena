package me.hapyl.fight.trigger.subscribe;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.State;
import me.hapyl.fight.trigger.Trigger;

public class GameChangeStateTrigger implements Trigger {

    public final GameInstance gameInstance;
    public final State state;

    public GameChangeStateTrigger(GameInstance gameInstance, State state) {
        this.gameInstance = gameInstance;
        this.state = state;
    }
}
