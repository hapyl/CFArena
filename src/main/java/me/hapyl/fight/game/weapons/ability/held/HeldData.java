package me.hapyl.fight.game.weapons.ability.held;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class HeldData extends PlayerData {

    private final long maxIdleDuration;

    protected int unit;
    protected long lastUse;

    public HeldData(GamePlayer player, long maxIdleDuration) {
        super(player);

        this.maxIdleDuration = maxIdleDuration;
    }

    public boolean isIdling() {
        return lastUse > 0L && System.currentTimeMillis() - lastUse >= getMaxIdleDurationCompensatePing();
    }

    public long getMaxIdleDuration() {
        return maxIdleDuration;
    }

    public long getMaxIdleDurationCompensatePing() {
        return maxIdleDuration + (175L * (1 + player.getPing() / 50));
    }

    @Override
    public void remove() {

    }

    public void addUnit(int unit) {
        this.lastUse = System.currentTimeMillis();
        this.unit += unit;
    }

    public int getUnit() {
        return unit;
    }
}
