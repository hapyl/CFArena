package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.LivingEntity;

public class ActiveGameEffect {

    private final LivingEntity entity;
    private final GameEffectType type;
    private int level;
    private int remainingTicks;

    public ActiveGameEffect(LivingEntity entity, GameEffectType type, int initTicks) {
        this.entity = entity;
        this.type = type;
        this.remainingTicks = initTicks;
        this.level = 0;

        startTicking();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public GameEffectType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void triggerUpdate() {
        this.type.getGameEffect().onUpdate(entity);
    }

    public void setRemainingTicks(int ticks) {
        this.remainingTicks = ticks;
    }

    public void addRemainingTicks(int ticks) {
        this.remainingTicks += ticks;
    }

    public void removeRemainingTicks(int ticks) {
        this.remainingTicks -= ticks;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void forceStop() {
        remainingTicks = 0;
        type.getGameEffect().onStop(entity);

        final EntityData data = EntityData.getEntityData(entity);
        data.clearEffect(type);
    }

    private void startTicking() {
        type.getGameEffect().onStart(entity);
        new GameTask() {
            @Override
            public void run() {

                // stop ticking
                if (remainingTicks <= 0) {
                    forceStop();
                    cancel();
                    return;
                }

                type.getGameEffect().onTick(entity, remainingTicks % 20);

                // actually tick down
                --remainingTicks;

            }
        }.runTaskTimer(0, 1);
    }

}
