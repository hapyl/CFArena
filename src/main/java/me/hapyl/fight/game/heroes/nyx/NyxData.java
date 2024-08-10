package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.util.Mth;

public class NyxData extends PlayerData {

    public static final int MAX_CHAOS_STACKS = 6;

    private int chaosStacks;

    public NyxData(GamePlayer player) {
        super(player);

        Debug.run(() -> {
            chaosStacks = 999;
        });
    }

    public int getChaosStacks() {
        return chaosStacks;
    }

    public void incrementChaosStacks() {
        chaosStacks = Mth.incrementMin(chaosStacks, MAX_CHAOS_STACKS);
    }

    public void decrementChaosStacks() {
        chaosStacks = Mth.decrementMax(chaosStacks, 0);
    }

    @Override
    public void remove() {

    }
}
