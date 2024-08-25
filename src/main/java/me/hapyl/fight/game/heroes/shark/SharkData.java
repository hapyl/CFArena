package me.hapyl.fight.game.heroes.shark;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.shark.SharkPassive;
import me.hapyl.fight.util.InternalCooldown;

public class SharkData extends PlayerData {

    private final Shark shark;
    private final SharkPassive passiveTalent;
    private final InternalCooldown icd = new InternalCooldown(150);

    private int bloodThirstStacks;

    public SharkData(Shark shark, GamePlayer player) {
        super(player);

        this.shark = shark;
        this.passiveTalent = shark.getPassiveTalent();

        this.bloodThirstStacks = 0;
    }

    public void addBloodThirstStack() {
        if (icd.isOnCooldown()) {
            return;
        }

        bloodThirstStacks = Math.clamp(bloodThirstStacks + 1, 0, passiveTalent.maxStacks);
        icd.startCooldown();
    }

    public int getBloodThirstStacks() {
        if (icd.timeSinceLastUseInTicks() > shark.getPassiveTalent().stackDuration) {
            remove();
        }

        return bloodThirstStacks;
    }

    @Override
    public void remove() {
        this.bloodThirstStacks = 0;
    }
}
