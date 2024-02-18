package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.archive.zealot.FerociousStrikes;

public class ZealotData extends PlayerData {

    protected int ferociousHits = 0;

    public ZealotData(GamePlayer player) {
        super(player);
    }

    public void incrementFerociousHits() {
        ferociousHits = Math.min(ferociousHits + 1, FerociousStrikes.maxStrikes);
    }

    @Override
    public void remove() {
    }
}
