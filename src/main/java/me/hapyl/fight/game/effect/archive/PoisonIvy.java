package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.LivingEntity;

public class PoisonIvy extends GameEffect {
    public PoisonIvy() {
        super("Poison Ivy");

        setPositive(false);
    }

    @Override
    public void onStart(LivingEntity entity) {
    }

    @Override
    public void onStop(LivingEntity entity) {

    }

    @Override
    public void onTick(LivingEntity entity, int tick) {

    }
}
