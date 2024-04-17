package me.hapyl.fight.game.heroes.harbinger;

import me.hapyl.fight.game.entity.LivingGameEntity;

public class RiptideData {

    private final LivingGameEntity entity;
    private long affectTick;    // how many ticks effects are active for
    private long lastHitMillis; // how many millis since last hit

    public RiptideData(LivingGameEntity entity) {
        this.entity = entity;
        this.affectTick = 0L;
        this.lastHitMillis = 0L;
    }

    public long getAffectTick() {
        return affectTick;
    }

    public void setAffectTick(long affectTick) {
        this.affectTick = affectTick;
    }

    public void tick() {
        boolean isDead = entity.isDead();

        if (isDead) {
            this.affectTick = 0;
        }
        else if (this.affectTick > 0) {
            this.affectTick--;
        }
    }

    public LivingGameEntity getEntity() {
        return entity;
    }

    public boolean isExpired() {
        return affectTick <= 0;
    }

    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastHitMillis) < (RiptideStatus.RIPTIDE_PERIOD * 50);
    }

    public void markLastHit() {
        this.lastHitMillis = System.currentTimeMillis();
    }
}
