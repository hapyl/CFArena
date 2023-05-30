package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.LivingEntity;

public class FallDamageResistance extends GameEffect {

    public FallDamageResistance() {
        super("Fall Damage Resistance");
        this.setDescription("Negates all fall damage until it's taken.");
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {

    }

    @Override
    public void onStart(LivingEntity entity) {

    }

    @Override
    public void onStop(LivingEntity entity) {

    }
}
