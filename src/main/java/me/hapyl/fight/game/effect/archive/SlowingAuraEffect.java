package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class SlowingAuraEffect extends GameEffect {

    public final int COOLDOWN_MODIFIER = 2;

    public SlowingAuraEffect() {
        super("Slowing Aura");
        setDescription("Slows players and increases talent cooldowns.");
        setPositive(false);
    }

    @Override
    public void onStart(LivingEntity entity) {
        entity.addPotionEffect(PotionEffectType.SLOW.createEffect(10000, 1));
    }

    @Override
    public void onStop(LivingEntity entity) {
        entity.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onTick(LivingEntity entity, int tick) {
    }
}
