package me.hapyl.fight.game.heroes.archive.harbinger;

import me.hapyl.fight.game.GamePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
        boolean isDead = entity.isDead();

        if (entity instanceof Player player) {
            isDead = GamePlayer.getPlayer(player).isDead();
        }

        if (isDead) {
            this.affectTick = 0;
        }
        else if (this.affectTick > 0) {
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
