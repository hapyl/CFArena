package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.display.StringDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerEvent;

public class ActiveGameEffect {

    private final GameEntity entity;
    private final GameEffectType type;
    private int level;
    private int remainingTicks;

    public ActiveGameEffect(GameEntity entity, GameEffectType type, int initTicks) {
        this.entity = entity;
        this.type = type;
        this.remainingTicks = initTicks;
        this.level = 0;

        startTicking();
    }

    public GameEntity getEntity() {
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
        entity.getData().clearEffect(type);
    }

    public <T extends PlayerEvent> void processEvent(T ev) {
    }

    private void startTicking() {
        final GameEffect effect = type.getGameEffect();
        final StringDisplay display = effect.getDisplay();

        effect.onStart(entity);

        if (display != null) {
            display.display(entity.getEyeLocation());
        }

        new GameTask() {
            @Override
            public void run() {

                // stop ticking
                if (remainingTicks <= 0) {
                    forceStop();
                    cancel();
                    return;
                }

                effect.onTick(entity, remainingTicks % 20);

                // actually tick down
                --remainingTicks;

            }
        }.runTaskTimer(0, 1);
    }

}
