package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Stun extends GameEffect {

    private final Map<LivingEntity, Float> oldSpeed = new HashMap<>();

    public Stun() {
        super("Stun");
        setDescription("Stunned players cannot move or use their abilities. Effect will be cleared upon taking damage.");
        setPositive(false);
        setTalentBlocking(true);
        setEffectParticle(new EffectParticle(Particle.VILLAGER_ANGRY, 1));
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        displayParticles(entity.getLocation().add(0.0d, 1.0d, 0.0d), entity.getEntity());
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        oldSpeed.put(entity.getEntity(), entity.getWalkSpeed());

        entity.setWalkSpeed(0.0f);
        entity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(999999, 250));
        entity.setCanMove(false);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.setWalkSpeed(oldSpeed.getOrDefault(entity.getEntity(), 0.1f));
        entity.removePotionEffect(PotionEffectType.WEAKNESS);
        entity.setCanMove(true);
    }
}
