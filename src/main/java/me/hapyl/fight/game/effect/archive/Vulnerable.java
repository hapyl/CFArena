package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Vulnerable extends GameEffect implements Listener {

    private final double damageMultiplier = 1.5d;

    public Vulnerable() {
        super("Vulnerable");
        setDescription("Vulnerable entities take more damage.");
        setPositive(false);
    }

    @EventHandler()
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (entity.hasEffect(GameEffectType.VULNERABLE)) {
            ev.setDamage(ev.getDamage() * damageMultiplier);
        }
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(
                    entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                    entity,
                    new EffectParticle(Particle.VILLAGER_ANGRY, 1)
            );
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {

    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {

    }
}
