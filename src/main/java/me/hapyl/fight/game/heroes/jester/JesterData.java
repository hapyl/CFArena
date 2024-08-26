package me.hapyl.fight.game.heroes.jester;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.jester.MusicBoxEntity;

public class JesterData extends PlayerData {

    public MusicBoxEntity musicBox;

    public JesterData(GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {

    }
}
