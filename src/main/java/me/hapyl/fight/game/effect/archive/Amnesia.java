package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.potion.PotionEffectType;

public class Amnesia extends GameEffect {

    public Amnesia() {
        super("Amnesia");
        this.setDescription("Players will move randomly and their vision is disturbed.");
        this.setPositive(false);
    }

    @Override
    public void onTick(GameEntity entity, int tick) {

    }

    @Override
    public void onStart(GameEntity entity) {
        entity.addPotionEffect(PotionEffectType.CONFUSION, 99999, 1);
    }

    @Override
    public void onStop(GameEntity entity) {
        entity.removePotionEffect(PotionEffectType.CONFUSION);
    }
}
