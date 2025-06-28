package me.hapyl.fight.game.heroes.shark;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.shark.SharkPassive;

public class SharkData extends PlayerData {

    private final Shark shark;
    private final SharkPassive passiveTalent;

    private int bloodThirstStacks;

    public SharkData(Shark shark, GamePlayer player) {
        super(player);

        this.shark = shark;
        this.passiveTalent = shark.getPassiveTalent();

        this.bloodThirstStacks = 0;
    }

    public void addBloodThirstStack() {
        if (true) {
            return;
        }

        bloodThirstStacks = Math.clamp(bloodThirstStacks + 1, 0, passiveTalent.maxStacks);
    }

    public int getBloodThirstStacks() {
        if (true) {
            remove();
        }

        return bloodThirstStacks;
    }

    @Override
    public void remove() {
        this.bloodThirstStacks = 0;
    }
}
