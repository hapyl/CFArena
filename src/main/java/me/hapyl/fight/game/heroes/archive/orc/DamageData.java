package me.hapyl.fight.game.heroes.archive.orc;

import me.hapyl.fight.game.entity.GamePlayer;

public class DamageData {

    public static final long HITS_DECAY = 2_000;
    public static final long USE_DELAY = 30_000;
    public static final int HITS_THRESHOLD = 7;

    private final GamePlayer player;
    protected long lastHit;
    protected int hitsTaken;
    protected long lastUse;

    public DamageData(GamePlayer player) {
        this.player = player;
    }

    public boolean addHitAndCheck() {
        final long millis = System.currentTimeMillis();

        if (lastUse > 0 && millis - lastUse < USE_DELAY) {
            return false;
        }

        if (lastHit > 0 && millis - lastHit >= HITS_DECAY) {
            hitsTaken = 0;
        }

        lastHit = millis;
        hitsTaken++;

        if (hitsTaken >= HITS_THRESHOLD) {
            hitsTaken = 0;
            lastUse = millis;
            return true;
        }

        return false;
    }
}
