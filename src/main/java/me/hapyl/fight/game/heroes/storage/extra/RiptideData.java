package me.hapyl.fight.game.heroes.storage.extra;

import org.bukkit.entity.LivingEntity;

public class RiptideData {

    private final LivingEntity entity;
    private long affectTick;    // how many ticks effect is active for
    private long lastHitMillis; // how many millis since last hit

    public RiptideData(LivingEntity entity) {
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
        if (this.affectTick > 0) {
            this.affectTick--;
        }
    }

    public LivingEntity getEntity() {
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
