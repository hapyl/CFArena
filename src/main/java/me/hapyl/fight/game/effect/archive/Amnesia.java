package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Amnesia extends GameEffect {

    public Amnesia() {
        super("Amnesia");
        this.setDescription("Players will move randomly and their vision is disturbed.");
        this.setPositive(false);
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
        entity.addPotionEffect(PotionEffectType.CONFUSION, 99999, 1);
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {
        entity.removePotionEffect(PotionEffectType.CONFUSION);
    }
}
