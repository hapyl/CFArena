package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Vulnerable extends Effect implements Listener {

    private final double damageMultiplier = 1.5d;

    public Vulnerable() {
        super("Vulnerable", EffectType.NEGATIVE);

        setDescription("""
                Vulnerable entities take more damage.
                """);
    }

    @EventHandler()
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (entity.hasEffect(Effects.VULNERABLE)) {
            ev.multiplyDamage(damageMultiplier);
        }
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
        if (tick % 20 == 5) {
            displayParticles(
                    entity.getEyeLocation().add(0.0d, 0.5d, 0.0d),
                    entity,
                    new EffectParticle(Particle.VILLAGER_ANGRY, 1)
            );
        }
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {

    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {

    }
}
