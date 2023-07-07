package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class Amnesia extends GameEffect {

    public Amnesia() {
        super("Amnesia");
        this.setDescription("Players will move randomly and their vision is disturbed.");
        this.setPositive(false);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {

    }

    @Override
    public void onStart(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.CONFUSION.createEffect(99999, 1));
    }

    @Override
    public void onStop(LivingEntity entity) {
        entity.removePotionEffect(PotionEffectType.CONFUSION);
    }
}
