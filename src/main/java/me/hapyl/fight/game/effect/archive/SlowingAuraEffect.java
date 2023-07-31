package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.potion.PotionEffectType;

public class SlowingAuraEffect extends GameEffect {

    public final int COOLDOWN_MODIFIER = 2;

    public SlowingAuraEffect() {
        super("Slowing Aura");
        setDescription("Slows players and increases talent cooldowns.");
        setPositive(false);
    }

    @Override
    public void onStart(GameEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW, 10000, 1);
    }

    @Override
    public void onStop(GameEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onTick(GameEntity entity, int tick) {
    }
}
