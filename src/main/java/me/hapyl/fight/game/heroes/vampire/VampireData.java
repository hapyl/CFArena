package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.vampire.Vampire;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.entity.Player;

public class VampireData {

    private final GamePlayer player;

    private double damageMultiplier;

    private int blood;
    private long durationMillis;
    private long usedAt;

    public VampireData(GamePlayer player) {
        this.player = player;
        this.damageMultiplier = 1.0d;
        this.usedAt = -1L;
        this.blood = 0;
    }

    public void setDamageMultiplier(double damageMultiplier, int duration) {
        this.damageMultiplier = Numbers.clamp(damageMultiplier, 1.0f, 50.0f);
        this.durationMillis = duration * 50L;
        this.usedAt = System.currentTimeMillis();
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = Numbers.clamp(blood, 0, Heroes.VAMPIRE.getHero(Vampire.class).MAX_BLOOD_STACKS);
    }

    public void addBlood(int i) {
        setBlood(getBlood() + i);
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public long getUsedAt() {
        return usedAt;
    }

    public void resetDamageMultiplier() {
        this.damageMultiplier = 1.0f;
        this.usedAt = -1L;
    }

    public boolean isExpired() {
        return ((System.currentTimeMillis() - usedAt) > durationMillis);
    }

    public long getTimeLeft() {
        return (usedAt + durationMillis) - System.currentTimeMillis();
    }

    public String getTimeLeftFormatter() {
        return BukkitUtils.roundTick((int) (getTimeLeft() / 50));
    }
}
