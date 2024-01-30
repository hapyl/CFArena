package me.hapyl.fight.game.heroes.archive.rogue;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class RogueData extends PlayerData {

    protected int secondWindCharges;

    public RogueData(GamePlayer player) {
        super(player);

        this.secondWindCharges = 1;
    }

    public void refreshSecondWindCharges() {
        this.secondWindCharges = 1;

        player.spawnBuffDisplay(Named.SECOND_WIND + " Refreshed", 30);
    }

    @Override
    public void remove() {
    }
}
