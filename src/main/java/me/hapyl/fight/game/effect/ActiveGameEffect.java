package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Ticking;

import javax.annotation.Nonnull;

public class ActiveGameEffect implements Ticking {

    private final LivingGameEntity entity;
    private final Effects type;
    private final Effect effect;
    private final int amplifier;
    private int level;
    private int remainingTicks;

    public ActiveGameEffect(LivingGameEntity entity, Effects type, int amplifier, int duration) {
        this.entity = entity;
        this.type = type;
        this.effect = type.getEffect();
        this.amplifier = amplifier;
        this.remainingTicks = duration;
        this.level = 0;

        start();
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    @Nonnull
    public Effects getType() {
        return type;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void triggerUpdate() {
        this.type.getEffect().onUpdate(entity);
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
            type.getEffect().onStop(entity, amplifier);
        }

        entity.getEntityData().clearEffect(type);
    }

    @Override
    public void tick() {
        // Stop ticking
        if (remainingTicks == 0 || entity.isDead()) {
            forceStop();
            return;
        }

        effect.onTick(entity, remainingTicks);

        // Do not tick infinite durations
        if (remainingTicks == -1) {
            return;
        }

        // Actually tick down
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public void forceStopIfNotInfinite() {
        if (remainingTicks != -1) {
            forceStop();
        }
    }

    public boolean isInfiniteDuration() {
        return remainingTicks == -1;
    }

    private void start() {
        final StringDisplay display = effect.getDisplay();

        effect.onStart(entity, amplifier, remainingTicks);

        if (display != null) {
            display.display(entity.getEyeLocation());
        }
    }

}
