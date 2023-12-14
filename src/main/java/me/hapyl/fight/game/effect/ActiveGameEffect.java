package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Ticking;

public class ActiveGameEffect implements Ticking {

    private final LivingGameEntity entity;
    private final GameEffectType type;
    private final GameEffect effect;
    private int level;
    private int remainingTicks;

    public ActiveGameEffect(LivingGameEntity entity, GameEffectType type, int initTicks) {
        this.entity = entity;
        this.type = type;
        this.effect = type.getGameEffect();
        this.remainingTicks = initTicks;
        this.level = 0;

        start();
    }

    public LivingGameEntity getEntity() {
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

    public void addRemainingTicks(int ticks) {
        this.remainingTicks += ticks;
    }

    public void removeRemainingTicks(int ticks) {
        this.remainingTicks -= ticks;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void setRemainingTicks(int ticks) {
        this.remainingTicks = ticks;
    }

    public void forceStop() {
        remainingTicks = 0;

        if (!entity.isDead()) {
            type.getGameEffect().onStop(entity);
        }

        entity.getData().clearEffect(type);
    }

    @Override
    public void tick() {
        // Stop ticking
        if (remainingTicks <= 0 || entity.isDead()) {
            forceStop();
            return;
        }

        effect.onTick(entity, remainingTicks % 20);

        // Actually tick down
        --remainingTicks;
    }

    private void start() {
        final StringDisplay display = effect.getDisplay();

        effect.onStart(entity);

        if (display != null) {
            display.display(entity.getEyeLocation());
        }
    }

}
